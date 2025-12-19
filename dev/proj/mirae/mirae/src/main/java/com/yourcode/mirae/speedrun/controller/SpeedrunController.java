package com.yourcode.mirae.speedrun.controller;

import com.yourcode.mirae.speedrun.dto.*;
import com.yourcode.mirae.speedrun.redis.GameMode;
import com.yourcode.mirae.speedrun.service.RankingService;
import com.yourcode.mirae.speedrun.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/speedrun")
@RequiredArgsConstructor
@Tag(name = "Speedrun", description = "Speedrun minigame API")
public class SpeedrunController {

    private final SessionService sessionService;
    private final RankingService rankingService;

    // ===================== Session Management =====================

    @Operation(summary = "Create Classic session", description = "Create a new Classic speedrun session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/classic/session")
    public ResponseEntity<SessionResponse> createClassicSession(
            @Valid @RequestBody CreateSessionRequest request,
            @Parameter(description = "User ID") @RequestHeader("X-User-Id") Long userId) {
        SessionResponse response = sessionService.createSession(GameMode.CLASSIC, request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create TagFocus session", description = "Create a new TagFocus speedrun session")
    @PostMapping("/tagfocus/session")
    public ResponseEntity<SessionResponse> createTagFocusSession(
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        SessionResponse response = sessionService.createSession(GameMode.TAGFOCUS, request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Create Retry session", description = "Create a new Retry speedrun session")
    @PostMapping("/retry/session")
    public ResponseEntity<SessionResponse> createRetrySession(
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        SessionResponse response = sessionService.createSession(GameMode.RETRY, request, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get session info", description = "Get session information by ID")
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(
            @PathVariable String sessionId) {
        SessionResponse response = sessionService.getSession(sessionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get session state", description = "Get current session state including remaining time")
    @GetMapping("/session/{sessionId}/state")
    public ResponseEntity<SessionStateResponse> getSessionState(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") Long userId) {
        SessionStateResponse response = sessionService.getSessionState(sessionId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Join session", description = "Join an existing session")
    @PostMapping("/session/{sessionId}/join")
    public ResponseEntity<SessionResponse> joinSession(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") Long userId) {
        SessionResponse response = sessionService.joinSession(sessionId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Leave session", description = "Leave current session")
    @PostMapping("/session/{sessionId}/leave")
    public ResponseEntity<SessionResponse> leaveSession(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") Long userId) {
        SessionResponse response = sessionService.leaveSession(sessionId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Start session", description = "Start the session (owner only)")
    @PostMapping("/session/{sessionId}/start")
    public ResponseEntity<SessionResponse> startSession(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") Long userId) {
        SessionResponse response = sessionService.startSession(sessionId, userId);
        return ResponseEntity.ok(response);
    }

    // ===================== Game Play =====================

    @Operation(summary = "Submit problem", description = "Submit a problem solution claim")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Problem submitted successfully"),
            @ApiResponse(responseCode = "400", description = "Session not running"),
            @ApiResponse(responseCode = "409", description = "Duplicate submission")
    })
    @PostMapping("/session/{sessionId}/submit")
    public ResponseEntity<SubmitResponse> submitProblem(
            @PathVariable String sessionId,
            @Valid @RequestBody SubmitRequest request,
            @RequestHeader("X-User-Id") Long userId) {
        SubmitResponse response = sessionService.submitProblem(sessionId, userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get my status", description = "Get current user's status in the session")
    @GetMapping("/session/{sessionId}/my-status")
    public ResponseEntity<UserStatusResponse> getMyStatus(
            @PathVariable String sessionId,
            @RequestHeader("X-User-Id") Long userId) {
        UserStatusResponse response = sessionService.getUserStatus(sessionId, userId);
        return ResponseEntity.ok(response);
    }

    // ===================== Leaderboard & Ranking =====================

    @Operation(summary = "Get session leaderboard", description = "Get the leaderboard for a session")
    @GetMapping("/session/{sessionId}/leaderboard")
    public ResponseEntity<List<LeaderboardEntry>> getSessionLeaderboard(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "10") int limit) {
        List<LeaderboardEntry> leaderboard = rankingService.getSessionLeaderboard(sessionId, limit);
        return ResponseEntity.ok(leaderboard);
    }

    @Operation(summary = "Get global ranking", description = "Get the global ranking")
    @GetMapping("/ranking/global")
    public ResponseEntity<RankingResponse> getGlobalRanking(
            @RequestParam(defaultValue = "100") int limit) {
        RankingResponse response = rankingService.getGlobalRanking(limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get weekly ranking", description = "Get the weekly ranking")
    @GetMapping("/ranking/weekly")
    public ResponseEntity<RankingResponse> getWeeklyRanking(
            @RequestParam(defaultValue = "100") int limit) {
        RankingResponse response = rankingService.getWeeklyRanking(limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get monthly ranking", description = "Get the monthly ranking")
    @GetMapping("/ranking/monthly")
    public ResponseEntity<RankingResponse> getMonthlyRanking(
            @RequestParam(defaultValue = "100") int limit) {
        RankingResponse response = rankingService.getMonthlyRanking(limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get my ranking", description = "Get current user's ranking info")
    @GetMapping("/ranking/my")
    public ResponseEntity<LeaderboardEntry> getMyRanking(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "global") String type) {
        LeaderboardEntry response = rankingService.getMyRanking(userId, type);
        return ResponseEntity.ok(response);
    }
}
