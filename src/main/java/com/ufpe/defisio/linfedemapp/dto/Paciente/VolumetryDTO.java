package com.ufpe.defisio.linfedemapp.dto.Paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolumetryDTO {
    private String referenceArm;
    private String affectedArm;
    private List<Double> volumesReferencia;
    private List<Double> volumesAfetado;
    private Double volumeDifference;
}
