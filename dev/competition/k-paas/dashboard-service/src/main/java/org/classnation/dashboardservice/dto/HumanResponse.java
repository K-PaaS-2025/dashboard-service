package org.classnation.dashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.classnation.dashboardservice.entity.HomeSize;
import org.classnation.dashboardservice.entity.Mobility;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumanResponse {

    private String humanUuid;
    private String name;
    private String contact;
    private String address;
    private HomeSize homeSize;
    private Mobility mobility;
    private Boolean petExperience;
    private Integer outingHours;
    private Boolean initialConsulted;
    private Boolean isMatched;
}
