package org.classnation.dashboardservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Validation error: {}", requestId, ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                400,
                "Validation Error",
                "VALIDATION_ERROR",
                ex.getMessage()
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        String errorDetails = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    if (error instanceof FieldError) {
                        return ((FieldError) error).getField() + ": " + error.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .collect(Collectors.joining(", "));

        log.error("[{}] Method argument validation error: {}", requestId, errorDetails);

        ApiResponse<Void> response = ApiResponse.error(
                400,
                "Invalid Request Parameters",
                "INVALID_PARAMETERS",
                errorDetails
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorizedException(
            UnauthorizedException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Unauthorized: {}", requestId, ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                401,
                "Unauthorized",
                "AUTHENTICATION_FAILED",
                ex.getMessage()
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbiddenException(
            ForbiddenException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Forbidden: {}", requestId, ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                403,
                "Forbidden",
                "INSUFFICIENT_PERMISSIONS",
                ex.getMessage()
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Resource not found: {}", requestId, ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                404,
                "Resource Not Found",
                "RESOURCE_NOT_FOUND",
                ex.getMessage()
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflictException(
            ConflictException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Conflict: {}", requestId, ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(
                409,
                "Conflict",
                "STATE_CONFLICT",
                ex.getMessage()
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiResponse<Void>> handleExternalServiceException(
            ExternalServiceException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] External service error: {}", requestId, ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                502,
                "External Service Error",
                "EXTERNAL_SERVICE_FAILURE",
                ex.getMessage()
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(response);
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleServiceUnavailableException(
            ServiceUnavailableException ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Service unavailable: {}", requestId, ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                503,
                "Service Unavailable",
                "SERVICE_UNAVAILABLE",
                ex.getMessage()
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        String requestId = generateRequestId();
        log.error("[{}] Unexpected error: {}", requestId, ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                500,
                "Internal Server Error",
                "INTERNAL_ERROR",
                "An unexpected error occurred"
        ).withRequestId(requestId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
