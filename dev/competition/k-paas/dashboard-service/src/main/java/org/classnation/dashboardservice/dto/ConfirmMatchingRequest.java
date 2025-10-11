package org.classnation.dashboardservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmMatchingRequest {

    @NotNull
    private String humanUuid;

    @NotNull
    private String dogUuid;
}
