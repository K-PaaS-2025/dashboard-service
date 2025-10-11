package org.classnation.dashboardservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.client.ReportServiceClient;
import org.classnation.dashboardservice.client.dto.ReportServiceRegisterSeniorRequest;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.entity.Human;
import org.classnation.dashboardservice.exception.ResourceNotFoundException;
import org.classnation.dashboardservice.exception.ValidationException;
import org.classnation.dashboardservice.repository.HumanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class HumanService {

    private final HumanRepository humanRepository;
    private final ReportServiceClient reportServiceClient;

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    @Transactional
    public HumanResponse upsertHuman(String humanUuid, HumanUpsertRequest request) {
        log.info("Upserting human: {}", humanUuid);

        // Validate UUID format
        if (!UUID_PATTERN.matcher(humanUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + humanUuid);
        }

        Human human = humanRepository.findByHumanUuid(humanUuid)
                .orElse(Human.builder()
                        .humanUuid(humanUuid)
                        .initialConsulted(false)
                        .isMatched(false)
                        .build());

        // Update fields
        human.setName(request.getName());
        human.setContact(request.getContact());
        human.setAddress(request.getAddress());
        human.setHomeSize(request.getHomeSize());
        human.setMobility(request.getMobility());
        human.setPetExperience(request.getPetExperience());
        human.setOutingHours(request.getOutingHours());

        human = humanRepository.save(human);

        return mapToHumanResponse(human);
    }

    @Transactional(readOnly = true)
    public InitialConsultedResponse checkInitialConsulted(String humanUuid) {
        log.info("Checking initial consulted status for human: {}", humanUuid);

        if (!UUID_PATTERN.matcher(humanUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + humanUuid);
        }

        Human human = humanRepository.findByHumanUuid(humanUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Human not found: " + humanUuid));

        return InitialConsultedResponse.builder()
                .initialConsulted(human.getInitialConsulted())
                .build();
    }

    @Transactional
    public InitialConsultResponse registerInitialConsult(String humanUuid) {
        log.info("Registering initial consultation for human: {}", humanUuid);

        if (!UUID_PATTERN.matcher(humanUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + humanUuid);
        }

        Human human = humanRepository.findByHumanUuid(humanUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Human not found: " + humanUuid));

        // Call report-service to register senior
        List<Map<String, String>> conversationHistory = new ArrayList<>();
        Map<String, String> conversation = new HashMap<>();
        conversation.put("role", "system");
        conversation.put("content", "Initial consultation registered via dashboard");
        conversationHistory.add(conversation);

        ReportServiceRegisterSeniorRequest registerRequest = ReportServiceRegisterSeniorRequest.builder()
                .conversationHistory(conversationHistory)
                .userId(humanUuid)
                .build();

        reportServiceClient.registerSenior(registerRequest);

        // Update initial_consulted flag
        human.setInitialConsulted(true);
        human = humanRepository.save(human);

        return InitialConsultResponse.builder()
                .humanUuid(human.getHumanUuid())
                .initialConsulted(human.getInitialConsulted())
                .build();
    }

    @Transactional(readOnly = true)
    public MatchStatusResponse checkMatchStatus(String humanUuid) {
        log.info("Checking match status for human: {}", humanUuid);

        if (!UUID_PATTERN.matcher(humanUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + humanUuid);
        }

        Human human = humanRepository.findByHumanUuid(humanUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Human not found: " + humanUuid));

        return MatchStatusResponse.builder()
                .isMatched(human.getIsMatched())
                .build();
    }

    public LatestDangerResponse getLatestDanger(String humanUuid) {
        log.info("Getting latest danger report for human: {}", humanUuid);

        if (!UUID_PATTERN.matcher(humanUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + humanUuid);
        }

        // Verify human exists
        if (!humanRepository.existsByHumanUuid(humanUuid)) {
            throw new ResourceNotFoundException("Human not found: " + humanUuid);
        }

        // Proxy to report-service
        return reportServiceClient.getLatestDangerReport(humanUuid);
    }

    private HumanResponse mapToHumanResponse(Human human) {
        return HumanResponse.builder()
                .humanUuid(human.getHumanUuid())
                .name(human.getName())
                .contact(human.getContact())
                .address(human.getAddress())
                .homeSize(human.getHomeSize())
                .mobility(human.getMobility())
                .petExperience(human.getPetExperience())
                .outingHours(human.getOutingHours())
                .initialConsulted(human.getInitialConsulted())
                .isMatched(human.getIsMatched())
                .build();
    }
}
