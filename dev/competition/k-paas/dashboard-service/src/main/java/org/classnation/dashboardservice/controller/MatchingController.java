package org.classnation.dashboardservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.dto.*;
import org.classnation.dashboardservice.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
public class MatchingController {

    private final MatchingService matchingService;

    @GetMapping("/matching/{human_uuid}/candidates")
    public ResponseEntity<ApiResponse<MatchingCandidatesResponse>> getMatchingCandidates(
            @PathVariable("human_uuid") String humanUuid,
            @RequestParam(value = "top", required = false, defaultValue = "3") Integer top) {

        log.info("GET /api/dashboard/matching/{}/candidates?top={}", humanUuid, top);

        MatchingCandidatesResponse response = matchingService.getMatchingCandidates(humanUuid, top);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/matchings/confirm")
    public ResponseEntity<ApiResponse<ConfirmMatchingResponse>> confirmMatching(
            @Valid @RequestBody ConfirmMatchingRequest request) {

        log.info("POST /api/dashboard/matchings/confirm - human={}, dog={}",
                request.getHumanUuid(), request.getDogUuid());

        ConfirmMatchingResponse response = matchingService.confirmMatching(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
