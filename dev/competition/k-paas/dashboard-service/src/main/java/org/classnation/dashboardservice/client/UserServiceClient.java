package org.classnation.dashboardservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.client.dto.VerifyTokenResponse;
import org.classnation.dashboardservice.exception.ExternalServiceException;
import org.classnation.dashboardservice.exception.ForbiddenException;
import org.classnation.dashboardservice.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.user-service.url:http://localhost:8080}")
    private String userServiceUrl;

    @CircuitBreaker(name = "userService", fallbackMethod = "verifyTokenFallback")
    @Retry(name = "userService")
    public VerifyTokenResponse verifyToken(String token) {
        log.info("Verifying token with user-service");

        try {
            return webClientBuilder.build()
                    .post()
                    .uri(userServiceUrl + "/api/auth/verify")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(
                            status -> status.equals(HttpStatus.UNAUTHORIZED),
                            response -> Mono.error(new UnauthorizedException("Token validation failed"))
                    )
                    .onStatus(
                            status -> status.equals(HttpStatus.FORBIDDEN),
                            response -> Mono.error(new ForbiddenException("Insufficient permissions"))
                    )
                    .onStatus(
                            status -> status.isError(),
                            response -> Mono.error(new ExternalServiceException("User service error"))
                    )
                    .bodyToMono(VerifyTokenResponse.class)
                    .timeout(Duration.ofMillis(3000))
                    .block();
        } catch (UnauthorizedException | ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error calling user-service verify endpoint", e);
            throw new ExternalServiceException("Failed to verify token with user-service", e);
        }
    }

    private VerifyTokenResponse verifyTokenFallback(String token, Exception ex) {
        log.error("Circuit breaker opened for user-service: {}", ex.getMessage());
        throw new ExternalServiceException("User service is currently unavailable", ex);
    }
}
