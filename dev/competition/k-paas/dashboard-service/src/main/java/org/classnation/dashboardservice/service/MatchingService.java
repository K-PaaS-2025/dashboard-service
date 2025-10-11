package org.classnation.dashboardservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.client.ReportServiceClient;
import org.classnation.dashboardservice.client.dto.ReportServiceConfirmAdoptionRequest;
import org.classnation.dashboardservice.client.dto.ReportServiceConfirmAdoptionResponse;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.entity.Dog;
import org.classnation.dashboardservice.entity.Human;
import org.classnation.dashboardservice.entity.Matching;
import org.classnation.dashboardservice.exception.ConflictException;
import org.classnation.dashboardservice.exception.ResourceNotFoundException;
import org.classnation.dashboardservice.exception.ValidationException;
import org.classnation.dashboardservice.repository.DogRepository;
import org.classnation.dashboardservice.repository.HumanRepository;
import org.classnation.dashboardservice.repository.MatchingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final HumanRepository humanRepository;
    private final DogRepository dogRepository;
    private final MatchingRepository matchingRepository;
    private final ReportServiceClient reportServiceClient;

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    public MatchingCandidatesResponse getMatchingCandidates(String humanUuid, Integer top) {
        log.info("Getting matching candidates for human: {}, top: {}", humanUuid, top);

        // Validate UUID format
        if (!UUID_PATTERN.matcher(humanUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + humanUuid);
        }

        // Validate top parameter (1-3 range)
        if (top == null) {
            top = 3;
        }
        if (top < 1 || top > 3) {
            throw new ValidationException("Top parameter must be between 1 and 3");
        }

        // Verify human exists
        if (!humanRepository.existsByHumanUuid(humanUuid)) {
            throw new ResourceNotFoundException("Human not found: " + humanUuid);
        }

        // Proxy to report-service
        return reportServiceClient.getMatchingCandidates(humanUuid, top);
    }

    @Transactional
    public ConfirmMatchingResponse confirmMatching(ConfirmMatchingRequest request) {
        log.info("Confirming matching: human={}, dog={}", request.getHumanUuid(), request.getDogUuid());

        String humanUuid = request.getHumanUuid();
        String dogUuid = request.getDogUuid();

        // Validate UUID formats
        if (!UUID_PATTERN.matcher(humanUuid).matches()) {
            throw new ValidationException("Invalid human UUID format: " + humanUuid);
        }
        if (!UUID_PATTERN.matcher(dogUuid).matches()) {
            throw new ValidationException("Invalid dog UUID format: " + dogUuid);
        }

        // Check if matching already exists
        if (matchingRepository.existsByHumanUuidAndDogUuid(humanUuid, dogUuid)) {
            throw new ConflictException("Matching already exists for this human and dog");
        }

        // Get human and verify not already matched
        Human human = humanRepository.findByHumanUuid(humanUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Human not found: " + humanUuid));

        if (human.getIsMatched()) {
            throw new ConflictException("Human is already matched with another dog");
        }

        // Get dog and verify not already adopted
        Dog dog = dogRepository.findByDogUuid(dogUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found: " + dogUuid));

        if (dog.getIsAdopted()) {
            throw new ConflictException("Dog is already adopted by another person");
        }

        // Update human and dog status
        human.setIsMatched(true);
        dog.setIsAdopted(true);
        humanRepository.save(human);
        dogRepository.save(dog);

        // Call report-service to confirm adoption
        ReportServiceConfirmAdoptionRequest reportRequest = ReportServiceConfirmAdoptionRequest.builder()
                .humanUuid(humanUuid)
                .dogUuid(dogUuid)
                .build();

        ReportServiceConfirmAdoptionResponse reportResponse =
                reportServiceClient.confirmAdoption(reportRequest);

        // Create matching record
        Matching matching = Matching.builder()
                .humanUuid(humanUuid)
                .dogUuid(dogUuid)
                .reportId(reportResponse.getReportId())
                .isDanger(reportResponse.getIsDanger())
                .matchedAt(LocalDateTime.now())
                .build();

        matching = matchingRepository.save(matching);

        log.info("Matching confirmed successfully: matchingId={}, reportId={}",
                matching.getMatchingId(), matching.getReportId());

        return ConfirmMatchingResponse.builder()
                .matchingId(matching.getMatchingId())
                .humanUuid(matching.getHumanUuid())
                .dogUuid(matching.getDogUuid())
                .reportId(matching.getReportId())
                .isDanger(matching.getIsDanger())
                .matchedAt(matching.getMatchedAt())
                .build();
    }
}
