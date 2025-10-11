package org.classnation.dashboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.classnation.dashboardservice.entity.Activity;
import org.classnation.dashboardservice.entity.DogSize;
import org.classnation.dashboardservice.entity.Temperament;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DogResponse {

    private String dogUuid;
    private String shelterName;
    private String name;
    private DogSize size;
    private Activity activity;
    private Temperament temperament;
    private String diseases;
    private Boolean isAdopted;
}
