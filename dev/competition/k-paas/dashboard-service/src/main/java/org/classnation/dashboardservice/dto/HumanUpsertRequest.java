package org.classnation.dashboardservice.dto;

import jakarta.validation.constraints.NotNull;
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
public class HumanUpsertRequest {

    private String name;
    private String contact;
    private String address;

    @NotNull
    private HomeSize homeSize;

    @NotNull
    private Mobility mobility;

    @NotNull
    private Boolean petExperience;

    @NotNull
    private Integer outingHours;
}
