package com.ufpe.defisio.linfedemapp.services;

import com.ufpe.defisio.linfedemapp.domain.paciente.Paciente;
import com.ufpe.defisio.linfedemapp.domain.paciente.DadosMensuracao;
import com.ufpe.defisio.linfedemapp.domain.user.User;
import com.ufpe.defisio.linfedemapp.dto.Paciente.*;
import com.ufpe.defisio.linfedemapp.repositories.PacienteRepository;
import com.ufpe.defisio.linfedemapp.repositories.DadosMensuracaoRepository;
import com.ufpe.defisio.linfedemapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final DadosMensuracaoRepository dadosMensuracaoRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Paciente buscarPacientePorId(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        // open-in-view=false: inicializa as coleções LAZY enquanto a sessão está aberta,
        // evitando LazyInitializationException na serialização JSON
        paciente.getProcedimentos().size();
        paciente.getAlteracoesCutaneas().size();
        return paciente;
    }

    @Transactional
    public void deletarPaciente(UUID idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        dadosMensuracaoRepository.deleteAllByPacienteId(idPaciente);
        pacienteRepository.delete(paciente);
    }

    public Paciente addPaciente(PacienteRequestDTO dto) {
        User especialista = userRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Especialista não encontrado"));

        Paciente paciente = new Paciente();
        paciente.setNome(dto.getNome());
        paciente.setDataNascimento(dto.getDataNascimento());
        paciente.setEndereco(dto.getEndereco());
        paciente.setTelefone(dto.getTelefone());
        paciente.setPesoCorporal(dto.getPesoCorporal());
        paciente.setAltura(dto.getAltura());
        paciente.setNivelAtividadeFisica(dto.getNivelAtividadeFisica());
        paciente.setEstadoCivil(dto.getEstadoCivil());
        paciente.setOcupacao(dto.getOcupacao());
        paciente.setDataDiagnostiCancer(dto.getDataDiagnostiCancer());

        paciente.setProcedimentos(dto.getProcedimentos());
        paciente.setAlteracoesCutaneas(dto.getAlteracoesCutaneas());
        paciente.setQueixasMusculoesqueleticas(dto.getQueixasMusculoesqueleticas());
        paciente.setSintomasLinfedema(dto.getSintomasLinfedema());
        paciente.setSinalCacifo(dto.getSinalCacifo());
        paciente.setSinalCascaLaranja(dto.getSinalCascaLaranja());
        paciente.setSinalStemmer(dto.getSinalStemmer());

        paciente.setRadioterapia(dto.getRadioterapia());
        paciente.setCirurgia(dto.getCirurgia());
        paciente.setDisseccaoAxilar(dto.getDisseccaoAxilar());
        paciente.setHormonoterapia(dto.getHormonoterapia());
        paciente.setQuimioterapia(dto.getQuimioterapia());

        paciente.setObservacaoPaciente(dto.getObservacaoPaciente());

        paciente.setUsuario(especialista);

        return pacienteRepository.save(paciente);
    }

    public DadosMensuracao addDadosMensuracao(UUID pacienteId, MeasurementsDTO dto) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        VolumetryDTO vol = dto.getVolumetry();
        PerimetryDTO peri = dto.getPerimetry();

        DadosMensuracao dados = new DadosMensuracao();
        dados.setPaciente(paciente);
        dados.setTipoReferencia(dto.getTipoReferencia());
        dados.setObservacaoMedicao(dto.getObservacaoMedicao());
        dados.setDataAvaliacao(LocalDate.now());
        dados.setReferenceArm(vol.getReferenceArm());
        dados.setAffectedArm(vol.getAffectedArm());
        dados.setVolumesReferencia(vol.getVolumesReferencia());
        dados.setVolumesAfetado(vol.getVolumesAfetado());
        dados.setVolumeDifference(vol.getVolumeDifference());
        dados.setPontosRef(peri.getPontosRef());
        dados.setLeftArmInputs(peri.getLeftArmInputs());
        dados.setRightArmInputs(peri.getRightArmInputs());
        dados.setLeftArmComprimento(peri.getLeftArmComprimento());
        dados.setRightArmComprimento(peri.getRightArmComprimento());
        dados.setDifferences(peri.getDifferences());

        return dadosMensuracaoRepository.save(dados);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> listarPacientesDTO(UUID usuarioId) {
        List<Paciente> pacientes = pacienteRepository.findByUsuarioId(usuarioId);

        return pacientes.stream().map(paciente -> new PatientDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getDataNascimento(),
                paciente.getEndereco(),
                paciente.getTelefone(),
                paciente.getPesoCorporal(),
                paciente.getAltura(),
                paciente.getNivelAtividadeFisica(),
                paciente.getEstadoCivil(),
                paciente.getOcupacao(),
                paciente.getDataDiagnostiCancer(),
                // open-in-view=false: copia as coleções LAZY para listas simples,
                // forçando a inicialização dentro da transação e desacoplando do proxy
                // (do contrário a serialização JSON falha com LazyInitializationException)
                paciente.getProcedimentos() != null
                        ? new ArrayList<>(paciente.getProcedimentos())
                        : new ArrayList<String>(),
                paciente.getAlteracoesCutaneas() != null
                        ? new ArrayList<>(paciente.getAlteracoesCutaneas())
                        : new ArrayList<String>(),
                paciente.getQueixasMusculoesqueleticas(),
                paciente.getSintomasLinfedema(),
                paciente.getSinalCacifo(),
                paciente.getSinalCascaLaranja(),
                paciente.getSinalStemmer(),
                paciente.getRadiotherapyDTO(),
                paciente.getSurgeryDTO(),
                paciente.getAxillaryDissectionDTO(),
                paciente.getHormonoterapyDTO(),
                paciente.getDetalhesHormonoterapia(),
                paciente.getQuimioterapyDTO(),
                paciente.getObservacaoPaciente()
        )).toList();
    }

    @Transactional(readOnly = true)
    public List<DadosMensuracao> listarMensuracoesPorPaciente(UUID pacienteId, UUID usuarioId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (!paciente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Este paciente não pertence ao usuário informado");
        }

        return dadosMensuracaoRepository.findAllByPacienteId(pacienteId);
    }
}
