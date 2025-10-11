package org.classnation.dashboardservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.service.DogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/dogs")
@RequiredArgsConstructor
@Slf4j
public class DogController {

    private final DogService dogService;

    @PutMapping("/{dog_uuid}")
    public ResponseEntity<ApiResponse<DogResponse>> upsertDog(
            @PathVariable("dog_uuid") String dogUuid,
            @Valid @RequestBody DogUpsertRequest request) {

        log.info("PUT /api/dashboard/dogs/{} - Upsert dog", dogUuid);

        DogResponse response = dogService.upsertDog(dogUuid, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{dog_uuid}")
    public ResponseEntity<ApiResponse<DogResponse>> getDog(
            @PathVariable("dog_uuid") String dogUuid) {

        log.info("GET /api/dashboard/dogs/{}", dogUuid);

        DogResponse response = dogService.getDog(dogUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{dog_uuid}/adoption-status")
    public ResponseEntity<ApiResponse<AdoptionStatusResponse>> checkAdoptionStatus(
            @PathVariable("dog_uuid") String dogUuid) {

        log.info("GET /api/dashboard/dogs/{}/adoption-status", dogUuid);

        AdoptionStatusResponse response = dogService.checkAdoptionStatus(dogUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{dog_uuid}/adoption-status")
    public ResponseEntity<ApiResponse<UpdateAdoptionStatusResponse>> updateAdoptionStatus(
            @PathVariable("dog_uuid") String dogUuid,
            @Valid @RequestBody UpdateAdoptionStatusRequest request) {

        log.info("PUT /api/dashboard/dogs/{}/adoption-status - Update to {}", dogUuid, request.getIsAdopted());

        UpdateAdoptionStatusResponse response = dogService.updateAdoptionStatus(dogUuid, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
