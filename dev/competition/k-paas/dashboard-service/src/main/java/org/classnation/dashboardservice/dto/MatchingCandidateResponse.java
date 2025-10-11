package org.classnation.dashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingCandidateResponse {
    private String dogUuid;
    private Double score;
    private String reason;
}
