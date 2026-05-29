package com.ufpe.defisio.linfedemapp.dto.Paciente;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta para as mensurações. Mantém exatamente os mesmos campos
 * (flat) que o frontend consome, evitando serializar a entidade
 * DadosMensuracao (que possui coleções LAZY + paciente aninhado e quebraria
 * a serialização JSON com open-in-view=false).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasurementResponseDTO {
    private UUID id;
    private LocalDate dataAvaliacao;
    private String referenceArm;
    private String affectedArm;
    private List<Double> volumesReferencia;
    private List<Double> volumesAfetado;
    private Double volumeDifference;
    private String pontosRef;
    private List<String> leftArmInputs;
    private List<String> rightArmInputs;
    private String leftArmComprimento;
    private String rightArmComprimento;
    private List<Double> differences;
    private String tipoReferencia;
    private String observacaoMedicao;
}
