package org.classnation.dashboardservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.classnation.dashboardservice.client.dto.ReportServiceConfirmAdoptionRequest;
import org.classnation.dashboardservice.client.dto.ReportServiceConfirmAdoptionResponse;
import org.classnation.dashboardservice.client.dto.ReportServiceRegisterDogRequest;
import org.classnation.dashboardservice.client.dto.ReportServiceRegisterSeniorRequest;
import org.classnation.dashboardservice.dto.LatestDangerResponse;
import org.classnation.dashboardservice.dto.MatchingCandidatesResponse;
import org.classnation.dashboardservice.exception.ExternalServiceException;
import org.classnation.dashboardservice.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${external.report-service.url:http://localhost:1112}")
    private String reportServiceUrl;

    @CircuitBreaker(name = "reportService", fallbackMethod = "registerSeniorFallback")
    @Retry(name = "reportService")
    public void registerSenior(ReportServiceRegisterSeniorRequest request) {
        log.info("Registering senior with report-service: {}", request.getUserId());

        try {
            webClientBuilder.build()
                    .post()
                    .uri(reportServiceUrl + "/report-service/register/seniors")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            HttpStatus::isError,
                            response -> Mono.error(new ExternalServiceException("Report service error"))
                    )
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofMillis(3000))
                    .block();
        } catch (Exception e) {
            log.error("Error calling report-service register senior endpoint", e);
            throw new ExternalServiceException("Failed to register senior with report-service", e);
        }
    }

    private void registerSeniorFallback(ReportServiceRegisterSeniorRequest request, Exception ex) {
        log.error("Circuit breaker opened for report-service registerSenior: {}", ex.getMessage());
        throw new ServiceUnavailableException("Report service is currently unavailable", ex);
    }

    @CircuitBreaker(name = "reportService", fallbackMethod = "registerDogFallback")
    @Retry(name = "reportService")
    public void registerDog(ReportServiceRegisterDogRequest request) {
        log.info("Registering dog with report-service: {}", request.getName());

        try {
            webClientBuilder.build()
                    .post()
                    .uri(reportServiceUrl + "/report-service/register/dogs")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            HttpStatus::isError,
                            response -> Mono.error(new ExternalServiceException("Report service error"))
                    )
                    .bodyToMono(Void.class)
                    .timeout(Duration.ofMillis(3000))
                    .block();
        } catch (Exception e) {
            log.error("Error calling report-service register dog endpoint", e);
            throw new ExternalServiceException("Failed to register dog with report-service", e);
        }
    }

    private void registerDogFallback(ReportServiceRegisterDogRequest request, Exception ex) {
        log.error("Circuit breaker opened for report-service registerDog: {}", ex.getMessage());
        throw new ServiceUnavailableException("Report service is currently unavailable", ex);
    }

    @CircuitBreaker(name = "reportService", fallbackMethod = "getMatchingCandidatesFallback")
    @Retry(name = "reportService")
    public MatchingCandidatesResponse getMatchingCandidates(String humanUuid, int top) {
        log.info("Getting matching candidates for human: {}, top: {}", humanUuid, top);

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(reportServiceUrl + "/report-service/matching/seniors/" + humanUuid + "?top=" + top)
                    .retrieve()
                    .onStatus(
                            HttpStatus::isError,
                            response -> Mono.error(new ExternalServiceException("Report service error"))
                    )
                    .bodyToMono(MatchingCandidatesResponse.class)
                    .timeout(Duration.ofMillis(3000))
                    .block();
        } catch (Exception e) {
            log.error("Error calling report-service matching endpoint", e);
            throw new ExternalServiceException("Failed to get matching candidates from report-service", e);
        }
    }

    private MatchingCandidatesResponse getMatchingCandidatesFallback(String humanUuid, int top, Exception ex) {
        log.error("Circuit breaker opened for report-service getMatchingCandidates: {}", ex.getMessage());
        throw new ServiceUnavailableException("Report service is currently unavailable", ex);
    }

    @CircuitBreaker(name = "reportService", fallbackMethod = "confirmAdoptionFallback")
    @Retry(name = "reportService")
    public ReportServiceConfirmAdoptionResponse confirmAdoption(ReportServiceConfirmAdoptionRequest request) {
        log.info("Confirming adoption with report-service: {} - {}", request.getHumanUuid(), request.getDogUuid());

        try {
            return webClientBuilder.build()
                    .post()
                    .uri(reportServiceUrl + "/report-service/adoptions/confirm")
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            HttpStatus::isError,
                            response -> Mono.error(new ExternalServiceException("Report service error"))
                    )
                    .bodyToMono(ReportServiceConfirmAdoptionResponse.class)
                    .timeout(Duration.ofMillis(3000))
                    .block();
        } catch (Exception e) {
            log.error("Error calling report-service confirm adoption endpoint", e);
            throw new ExternalServiceException("Failed to confirm adoption with report-service", e);
        }
    }

    private ReportServiceConfirmAdoptionResponse confirmAdoptionFallback(
            ReportServiceConfirmAdoptionRequest request, Exception ex) {
        log.error("Circuit breaker opened for report-service confirmAdoption: {}", ex.getMessage());
        throw new ServiceUnavailableException("Report service is currently unavailable", ex);
    }

    @CircuitBreaker(name = "reportService", fallbackMethod = "getLatestDangerReportFallback")
    @Retry(name = "reportService")
    public LatestDangerResponse getLatestDangerReport(String humanUuid) {
        log.info("Getting latest danger report for human: {}", humanUuid);

        try {
            return webClientBuilder.build()
                    .get()
                    .uri(reportServiceUrl + "/report-service/reports/" + humanUuid + "?is_danger=true&limit=1&sort=created_at:desc")
                    .retrieve()
                    .onStatus(
                            HttpStatus::isError,
                            response -> Mono.error(new ExternalServiceException("Report service error"))
                    )
                    .bodyToMono(LatestDangerResponse.class)
                    .timeout(Duration.ofMillis(3000))
                    .block();
        } catch (Exception e) {
            log.error("Error calling report-service latest danger endpoint", e);
            throw new ExternalServiceException("Failed to get latest danger report from report-service", e);
        }
    }

    private LatestDangerResponse getLatestDangerReportFallback(String humanUuid, Exception ex) {
        log.error("Circuit breaker opened for report-service getLatestDangerReport: {}", ex.getMessage());
        throw new ServiceUnavailableException("Report service is currently unavailable", ex);
    }
}
