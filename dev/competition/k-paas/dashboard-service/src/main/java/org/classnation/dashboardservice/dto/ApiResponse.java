package org.classnation.dashboardservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private Boolean success;
    private Integer code;
    private String message;
    private T data;
    private ErrorDetail error;
    private LocalDateTime timestamp;
    private String requestId;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message("OK")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message, String errorType, String errorDetail) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .error(ErrorDetail.builder()
                        .type(errorType)
                        .detail(errorDetail)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message, ErrorDetail error) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .error(error)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ApiResponse<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
