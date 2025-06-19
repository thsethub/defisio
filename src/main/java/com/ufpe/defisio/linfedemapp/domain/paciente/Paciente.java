package com.ufpe.defisio.linfedemapp.domain.paciente;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ufpe.defisio.linfedemapp.domain.user.User;
import com.ufpe.defisio.linfedemapp.dto.Paciente.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pacientes")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String nome;
    private String dataNascimento;
    private String endereco;
    private String telefone;
    private String pesoCorporal;
    private String altura;
    private String nivelAtividadeFisica;
    private String estadoCivil;
    private String ocupacao;
    private String dataDiagnostiCancer;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> procedimentos;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> alteracoesCutaneas;

    private String queixasMusculoesqueleticas;
    private String sintomasLinfedema;
    private String sinalCacifo;
    private String sinalCascaLaranja;
    private String sinalStemmer;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tipo", column = @Column(name = "quimioterapia_tipo")),
            @AttributeOverride(name = "duracao", column = @Column(name = "quimioterapia_duracao"))
    })
    private ProcedimentoDetalhado quimioterapia;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tipo", column = @Column(name = "radioterapia_tipo")),
            @AttributeOverride(name = "duracao", column = @Column(name = "radioterapia_duracao"))
    })
    private ProcedimentoDetalhado radioterapia;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tipo", column = @Column(name = "hormonoterapia_tipo")),
            @AttributeOverride(name = "duracao", column = @Column(name = "hormonoterapia_duracao"))
    })
    private ProcedimentoDetalhado hormonoterapia;


    private String detalhesHormonoterapia;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tipo", column = @Column(name = "cirurgia_tipo")),
            @AttributeOverride(name = "duracao", column = @Column(name = "cirurgia_duracao"))
    })
    private ProcedimentoDetalhado cirurgia;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "tipo", column = @Column(name = "disseccao_tipo")),
            @AttributeOverride(name = "duracao", column = @Column(name = "disseccao_duracao"))
    })
    private ProcedimentoDetalhado disseccaoAxilar;

    private String observacaoPaciente;

    // Relacionamento com especialista (usuário)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false )
    @JsonIgnore
    private User usuario;

    // Métodos de acesso para DTOs

    public RadiotherapyDTO getRadiotherapyDTO() {
        return radioterapia != null ? new RadiotherapyDTO(radioterapia.getTipo(), radioterapia.getDuracao()) : null;
    }

    public SurgeryDTO getSurgeryDTO() {
        return cirurgia != null ? new SurgeryDTO(cirurgia.getTipo(), cirurgia.getDuracao()) : null;
    }

    public AxillaryDissectionDTO getAxillaryDissectionDTO() {
        return disseccaoAxilar != null ? new AxillaryDissectionDTO(disseccaoAxilar.getTipo(), disseccaoAxilar.getDuracao()) : null;
    }

    public HormonoterapyDTO getHormonoterapyDTO() {
        return hormonoterapia != null ? new HormonoterapyDTO(hormonoterapia.getTipo(), hormonoterapia.getDuracao()) : null;
    }

    public QuimioterapyDTO getQuimioterapyDTO() {
        return quimioterapia != null ? new QuimioterapyDTO(quimioterapia.getTipo(), quimioterapia.getDuracao()) : null;
    }
}
