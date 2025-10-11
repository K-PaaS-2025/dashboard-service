package org.classnation.dashboardservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportServiceConfirmAdoptionResponse {
    private String reportId;
    private Boolean isDanger;
}
