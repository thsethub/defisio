package com.ufpe.defisio.linfedemapp.dto.Paciente;

import com.ufpe.defisio.linfedemapp.domain.paciente.ProcedimentoDetalhado;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PacienteRequestDTO {
    private UUID usuarioId;

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

    private List<String> procedimentos;
    private List<String> alteracoesCutaneas;

    private String queixasMusculoesqueleticas;
    private String sintomasLinfedema;
    private String sinalCacifo;
    private String sinalCascaLaranja;
    private String sinalStemmer;

    private ProcedimentoDetalhado radioterapia;
    private ProcedimentoDetalhado cirurgia;
    private ProcedimentoDetalhado disseccaoAxilar;

}