package org.classnation.dashboardservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Matching Management", description = "Adoption matching APIs (ADMIN only)")
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class MatchingController {

    private final MatchingService matchingService;

    @Operation(summary = "Get matching candidates", description = "Get top N matching dog candidates for a senior (proxied from report-service)")
    @GetMapping("/matching/{human_uuid}/candidates")
    public ResponseEntity<ApiResponse<MatchingCandidatesResponse>> getMatchingCandidates(
            @Parameter(description = "UUID of the human") @PathVariable("human_uuid") String humanUuid,
            @Parameter(description = "Number of candidates to return (1-3)") @RequestParam(value = "top", required = false, defaultValue = "3") Integer top) {

        log.info("GET /api/dashboard/matching/{}/candidates?top={}", humanUuid, top);

        MatchingCandidatesResponse response = matchingService.getMatchingCandidates(humanUuid, top);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "Confirm matching", description = "Confirm an adoption match between a senior and a dog")
    @PostMapping("/matchings/confirm")
    public ResponseEntity<ApiResponse<ConfirmMatchingResponse>> confirmMatching(
            @Valid @RequestBody ConfirmMatchingRequest request) {

        log.info("POST /api/dashboard/matchings/confirm - human={}, dog={}",
                request.getHumanUuid(), request.getDogUuid());

        ConfirmMatchingResponse response = matchingService.confirmMatching(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
