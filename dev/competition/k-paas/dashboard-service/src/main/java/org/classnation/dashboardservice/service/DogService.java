package org.classnation.dashboardservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.client.ReportServiceClient;
import org.classnation.dashboardservice.client.dto.ReportServiceRegisterDogRequest;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.entity.Dog;
import org.classnation.dashboardservice.exception.ResourceNotFoundException;
import org.classnation.dashboardservice.exception.ValidationException;
import org.classnation.dashboardservice.repository.DogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class DogService {

    private final DogRepository dogRepository;
    private final ReportServiceClient reportServiceClient;

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    @Transactional
    public DogResponse upsertDog(String dogUuid, DogUpsertRequest request) {
        log.info("Upserting dog: {}", dogUuid);

        // Validate UUID format
        if (!UUID_PATTERN.matcher(dogUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + dogUuid);
        }

        Dog dog = dogRepository.findByDogUuid(dogUuid)
                .orElse(Dog.builder()
                        .dogUuid(dogUuid)
                        .isAdopted(false)
                        .build());

        // Update fields
        dog.setShelterName(request.getShelterName());
        dog.setName(request.getName());
        dog.setSize(request.getSize());
        dog.setActivity(request.getActivity());
        dog.setTemperament(request.getTemperament());
        dog.setDiseases(request.getDiseases());

        dog = dogRepository.save(dog);

        // Register with report-service
        ReportServiceRegisterDogRequest registerRequest = ReportServiceRegisterDogRequest.builder()
                .name(dog.getName())
                .gender("Unknown") // Not tracked in dashboard
                .age(0) // Not tracked in dashboard
                .species("Unknown") // Not tracked in dashboard
                .size(mapDogSizeToString(dog.getSize()))
                .activityLevel(mapActivityToString(dog.getActivity()))
                .hasMedicalNeeds(dog.getDiseases() != null && !dog.getDiseases().isEmpty())
                .medicalDescription(dog.getDiseases())
                .personality(mapTemperamentToString(dog.getTemperament()))
                .build();

        reportServiceClient.registerDog(registerRequest);

        return mapToDogResponse(dog);
    }

    @Transactional(readOnly = true)
    public DogResponse getDog(String dogUuid) {
        log.info("Getting dog: {}", dogUuid);

        if (!UUID_PATTERN.matcher(dogUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + dogUuid);
        }

        Dog dog = dogRepository.findByDogUuid(dogUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found: " + dogUuid));

        return mapToDogResponse(dog);
    }

    @Transactional(readOnly = true)
    public AdoptionStatusResponse checkAdoptionStatus(String dogUuid) {
        log.info("Checking adoption status for dog: {}", dogUuid);

        if (!UUID_PATTERN.matcher(dogUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + dogUuid);
        }

        Dog dog = dogRepository.findByDogUuid(dogUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found: " + dogUuid));

        return AdoptionStatusResponse.builder()
                .isAdopted(dog.getIsAdopted())
                .build();
    }

    @Transactional
    public UpdateAdoptionStatusResponse updateAdoptionStatus(String dogUuid, UpdateAdoptionStatusRequest request) {
        log.info("Updating adoption status for dog: {} to {}", dogUuid, request.getIsAdopted());

        if (!UUID_PATTERN.matcher(dogUuid).matches()) {
            throw new ValidationException("Invalid UUID format: " + dogUuid);
        }

        Dog dog = dogRepository.findByDogUuid(dogUuid)
                .orElseThrow(() -> new ResourceNotFoundException("Dog not found: " + dogUuid));

        dog.setIsAdopted(request.getIsAdopted());
        dog = dogRepository.save(dog);

        // Note: Report service integration for status change reporting would go here
        // but the instructions indicate this should trigger a report generation
        // which is handled separately through the report-service

        return UpdateAdoptionStatusResponse.builder()
                .dogUuid(dog.getDogUuid())
                .isAdopted(dog.getIsAdopted())
                .build();
    }

    private DogResponse mapToDogResponse(Dog dog) {
        return DogResponse.builder()
                .dogUuid(dog.getDogUuid())
                .shelterName(dog.getShelterName())
                .name(dog.getName())
                .size(dog.getSize())
                .activity(dog.getActivity())
                .temperament(dog.getTemperament())
                .diseases(dog.getDiseases())
                .isAdopted(dog.getIsAdopted())
                .build();
    }

    private String mapDogSizeToString(org.classnation.dashboardservice.entity.DogSize size) {
        if (size == null) return "중형";
        return switch (size) {
            case SMALL -> "소형";
            case MEDIUM -> "중형";
            case LARGE -> "대형";
        };
    }

    private String mapActivityToString(org.classnation.dashboardservice.entity.Activity activity) {
        if (activity == null) return "보통";
        return switch (activity) {
            case LOW -> "낮음";
            case MEDIUM -> "보통";
            case HIGH -> "높음";
        };
    }

    private String mapTemperamentToString(org.classnation.dashboardservice.entity.Temperament temperament) {
        if (temperament == null) return "온순함";
        return switch (temperament) {
            case CALM -> "온순함";
            case ACTIVE -> "활발함";
            case SHY -> "수줍음";
        };
    }
}
