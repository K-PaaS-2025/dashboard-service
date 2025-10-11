package org.classnation.dashboardservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportServiceConfirmAdoptionRequest {
    private String humanUuid;
    private String dogUuid;
}
