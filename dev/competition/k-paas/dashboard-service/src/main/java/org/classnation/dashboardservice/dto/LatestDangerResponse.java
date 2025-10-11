package org.classnation.dashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LatestDangerResponse {
    private String reportId;
    private Boolean isDanger;
    private LocalDateTime createdAt;
    private String summary;
}
