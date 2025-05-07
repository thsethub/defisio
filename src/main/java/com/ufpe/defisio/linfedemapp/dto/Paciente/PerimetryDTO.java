package com.ufpe.defisio.linfedemapp.dto.Paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerimetryDTO {
    private String pontosRef;
    private List<String> leftArmInputs;
    private List<String> rightArmInputs;
    private String leftArmComprimento;
    private String rightArmComprimento;
    private List<Double> differences;
}