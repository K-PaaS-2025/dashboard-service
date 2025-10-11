package org.classnation.dashboardservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.service.HumanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard/humans")
@RequiredArgsConstructor
@Slf4j
public class HumanController {

    private final HumanService humanService;

    @PutMapping("/{human_uuid}")
    public ResponseEntity<ApiResponse<HumanResponse>> upsertHuman(
            @PathVariable("human_uuid") String humanUuid,
            @Valid @RequestBody HumanUpsertRequest request) {

        log.info("PUT /api/dashboard/humans/{} - Upsert human", humanUuid);

        HumanResponse response = humanService.upsertHuman(humanUuid, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{human_uuid}/initial-consulted")
    public ResponseEntity<ApiResponse<InitialConsultedResponse>> checkInitialConsulted(
            @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/initial-consulted", humanUuid);

        InitialConsultedResponse response = humanService.checkInitialConsulted(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{human_uuid}/initial-consult")
    public ResponseEntity<ApiResponse<InitialConsultResponse>> registerInitialConsult(
            @PathVariable("human_uuid") String humanUuid) {

        log.info("POST /api/dashboard/humans/{}/initial-consult", humanUuid);

        InitialConsultResponse response = humanService.registerInitialConsult(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{human_uuid}/initial-consult/report")
    public ResponseEntity<ApiResponse<Object>> getInitialConsultReport(
            @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/initial-consult/report - Proxy to report-service", humanUuid);

        // This would be proxied to report-service
        // For now, returning a placeholder response
        // In full implementation, this would call reportServiceClient.getReports(humanUuid, "INITIAL_CONSULT")
        return ResponseEntity.ok(ApiResponse.success(null, "Proxy endpoint - implement report-service call"));
    }

    @GetMapping("/{human_uuid}/match-status")
    public ResponseEntity<ApiResponse<MatchStatusResponse>> checkMatchStatus(
            @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/match-status", humanUuid);

        MatchStatusResponse response = humanService.checkMatchStatus(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{human_uuid}/danger/latest")
    public ResponseEntity<ApiResponse<LatestDangerResponse>> getLatestDanger(
            @PathVariable("human_uuid") String humanUuid) {

        log.info("GET /api/dashboard/humans/{}/danger/latest", humanUuid);

        LatestDangerResponse response = humanService.getLatestDanger(humanUuid);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
