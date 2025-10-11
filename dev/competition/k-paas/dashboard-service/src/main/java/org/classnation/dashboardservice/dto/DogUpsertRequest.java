package org.classnation.dashboardservice.dto;

import jakarta.validation.constraints.NotNull;
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
public class DogUpsertRequest {

    private String shelterName;

    @NotNull
    private String name;

    @NotNull
    private DogSize size;

    @NotNull
    private Activity activity;

    @NotNull
    private Temperament temperament;

    private String diseases;
}
