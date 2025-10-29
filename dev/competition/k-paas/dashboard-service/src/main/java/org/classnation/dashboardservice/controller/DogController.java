package org.classnation.dashboardservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.service.DogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Dog Management", description = "Dog management APIs (ADMIN only)")
@RestController
@RequestMapping("/api/dashboard/dogs")
@RequiredArgsConstructor
@Slf4j
public class DogController {

    private final DogService dogService;

    @Operation(summary = "Upsert dog", description = "Create or update dog information")
    @PutMapping("/{dog_uuid}")
    public ResponseEntity<ApiResponse<DogResponse>> upsertDog(
            @Parameter(description = "UUID of the dog") @PathVariable("dog_uuid") String dogUuid,
            @Valid @RequestBody DogUpsertRequest request) {

        log.info("PUT /api/dashboard/dogs/{} - Upsert dog", dogUuid);

        DogResponse response = dogService.upsertDog(dogUuid, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get dog", description = "Retrieve dog information by UUID")
    @GetMapping("/{dog_uuid}")
    public ResponseEntity<ApiResponse<DogResponse>> getDog(
            @Parameter(description = "UUID of the dog") @PathVariable("dog_uuid") String dogUuid) {

        log.info("GET /api/dashboard/dogs/{}", dogUuid);

        DogResponse response = dogService.getDog(dogUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Check adoption status", description = "Check if a dog is adopted")
    @GetMapping("/{dog_uuid}/adoption-status")
    public ResponseEntity<ApiResponse<AdoptionStatusResponse>> checkAdoptionStatus(
            @Parameter(description = "UUID of the dog") @PathVariable("dog_uuid") String dogUuid) {

        log.info("GET /api/dashboard/dogs/{}/adoption-status", dogUuid);

        AdoptionStatusResponse response = dogService.checkAdoptionStatus(dogUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Update adoption status", description = "Manually update a dog's adoption status")
    @PutMapping("/{dog_uuid}/adoption-status")
    public ResponseEntity<ApiResponse<UpdateAdoptionStatusResponse>> updateAdoptionStatus(
            @Parameter(description = "UUID of the dog") @PathVariable("dog_uuid") String dogUuid,
            @Valid @RequestBody UpdateAdoptionStatusRequest request) {

        log.info("PUT /api/dashboard/dogs/{}/adoption-status - Update to {}", dogUuid, request.getIsAdopted());

        UpdateAdoptionStatusResponse response = dogService.updateAdoptionStatus(dogUuid, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
