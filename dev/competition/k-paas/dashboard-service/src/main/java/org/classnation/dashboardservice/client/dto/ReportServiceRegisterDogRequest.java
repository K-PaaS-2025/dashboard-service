package org.classnation.dashboardservice.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportServiceRegisterDogRequest {
    private String name;
    private String gender;
    private Integer age;
    private String species;
    private String size;
    private String activityLevel;
    private Boolean hasMedicalNeeds;
    private String medicalDescription;
    private String personality;
}
