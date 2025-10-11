package org.classnation.dashboardservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportServiceRegisterSeniorRequest {
    private List<Map<String, String>> conversationHistory;
    private String userId;
}
