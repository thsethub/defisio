package com.ufpe.defisio.linfedemapp.domain.paciente;

import com.ufpe.defisio.linfedemapp.dto.Paciente.PerimetryDTO;
import com.ufpe.defisio.linfedemapp.dto.Paciente.VolumetryDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dados_mensuracao")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosMensuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    private LocalDate dataAvaliacao; // <-- DATA DA AVALIAÇÃO

    // Volumetria
    private String referenceArm;
    private String affectedArm;

    @ElementCollection
    private List<Double> volumesReferencia;

    @ElementCollection
    private List<Double> volumesAfetado;

    private Double volumeDifference;

    // Perimetria
    private String pontosRef;

    @ElementCollection
    private List<String> leftArmInputs;

    @ElementCollection
    private List<String> rightArmInputs;

    private String leftArmComprimento;
    private String rightArmComprimento;

    @ElementCollection
    private List<Double> differences;

    private String tipoReferencia;
    private String observacaoMedicao;

    public VolumetryDTO getVolumetryDTO() {
        return new VolumetryDTO(referenceArm, affectedArm, volumesReferencia, volumesAfetado, volumeDifference);
    }

    public PerimetryDTO getPerimetryDTO() {
        return new PerimetryDTO(pontosRef, leftArmInputs, rightArmInputs, leftArmComprimento, rightArmComprimento, differences);
    }
}
