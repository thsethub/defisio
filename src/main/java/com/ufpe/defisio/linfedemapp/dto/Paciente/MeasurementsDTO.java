package com.ufpe.defisio.linfedemapp.dto.Paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementsDTO {
    private VolumetryDTO volumetry;
    private PerimetryDTO perimetry;
    private String tipoReferencia;
    private String observacaoMedicao;
}