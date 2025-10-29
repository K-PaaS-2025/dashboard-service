package org.classnation.dashboardservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.service.HumanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Human Management", description = "Senior/Human management APIs (ADMIN only)")
@RestController
@RequestMapping("/api/dashboard/humans")
@RequiredArgsConstructor
@Slf4j
public class HumanController {

    private final HumanService humanService;

    @Operation(summary = "Upsert human", description = "Create or update senior/human information")
    @PutMapping("/{human_uuid}")
    public ResponseEntity<ApiResponse<HumanResponse>> upsertHuman(
            @Parameter(description = "UUID of the human") @PathVariable("human_uuid") String humanUuid,
            @Valid @RequestBody HumanUpsertRequest request) {

        log.info("PUT /api/dashboard/humans/{} - Upsert human", humanUuid);

        HumanResponse response = humanService.upsertHuman(humanUuid, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Check initial consultation status", description = "Check if a senior has completed initial consultation")
    @GetMapping("/{human_uuid}/initial-consulted")
    public ResponseEntity<ApiResponse<InitialConsultedResponse>> checkInitialConsulted(
            @Parameter(description = "UUID of the human") @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/initial-consulted", humanUuid);

        InitialConsultedResponse response = humanService.checkInitialConsulted(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Register initial consultation", description = "Mark a senior as having completed initial consultation")
    @PostMapping("/{human_uuid}/initial-consult")
    public ResponseEntity<ApiResponse<InitialConsultResponse>> registerInitialConsult(
            @Parameter(description = "UUID of the human") @PathVariable("human_uuid") String humanUuid) {

        log.info("POST /api/dashboard/humans/{}/initial-consult", humanUuid);

        InitialConsultResponse response = humanService.registerInitialConsult(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get initial consultation report", description = "Get the initial consultation report for a senior (proxied from report-service)")
    @GetMapping("/{human_uuid}/initial-consult/report")
    public ResponseEntity<ApiResponse<Object>> getInitialConsultReport(
            @Parameter(description = "UUID of the human") @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/initial-consult/report - Proxy to report-service", humanUuid);

        // This would be proxied to report-service
        // For now, returning a placeholder response
        // In full implementation, this would call reportServiceClient.getReports(humanUuid, "INITIAL_CONSULT")
        return ResponseEntity.ok(ApiResponse.success(null, "Proxy endpoint - implement report-service call"));
    }

    @Operation(summary = "Check match status", description = "Check if a senior is matched with a dog")
    @GetMapping("/{human_uuid}/match-status")
    public ResponseEntity<ApiResponse<MatchStatusResponse>> checkMatchStatus(
            @Parameter(description = "UUID of the human") @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/match-status", humanUuid);

        MatchStatusResponse response = humanService.checkMatchStatus(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Get latest danger report", description = "Get the latest danger report for a senior")
    @GetMapping("/{human_uuid}/danger/latest")
    public ResponseEntity<ApiResponse<LatestDangerResponse>> getLatestDanger(
            @Parameter(description = "UUID of the human") @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/danger/latest", humanUuid);

        LatestDangerResponse response = humanService.getLatestDanger(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
