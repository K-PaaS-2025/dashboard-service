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
public class UpdateAdoptionStatusRequest {

    @NotNull
    private Boolean isAdopted;

    private String note;
}
