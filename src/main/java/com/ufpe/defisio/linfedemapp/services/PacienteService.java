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
        // open-in-view=false: inicializa as coleções LAZY enquanto a sessão está
        // aberta. A entidade é retornada como está (nomes em PT) porque a tela de
        // histórico/detalhes e o PDF consomem exatamente esses campos.
        if (paciente.getProcedimentos() != null) {
            paciente.getProcedimentos().size();
        }
        if (paciente.getAlteracoesCutaneas() != null) {
            paciente.getAlteracoesCutaneas().size();
        }
        return paciente;
    }

    @Transactional
    public void deletarPaciente(UUID idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        dadosMensuracaoRepository.deleteAllByPacienteId(idPaciente);
        pacienteRepository.delete(paciente);
    }

    @Transactional
    public PatientDTO addPaciente(PacienteRequestDTO dto) {
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

        Paciente salvo = pacienteRepository.save(paciente);
        return toPatientDTO(salvo);
    }

    @Transactional
    public MeasurementResponseDTO addDadosMensuracao(UUID pacienteId, MeasurementsDTO dto) {
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

        DadosMensuracao salvo = dadosMensuracaoRepository.save(dados);
        return toMeasurementDTO(salvo);
    }

    @Transactional(readOnly = true)
    public List<PatientDTO> listarPacientesDTO(UUID usuarioId) {
        return pacienteRepository.findByUsuarioId(usuarioId).stream()
                .map(this::toPatientDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MeasurementResponseDTO> listarMensuracoesPorPaciente(UUID pacienteId, UUID usuarioId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (!paciente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Este paciente não pertence ao usuário informado");
        }

        return dadosMensuracaoRepository.findAllByPacienteId(pacienteId).stream()
                .map(this::toMeasurementDTO)
                .toList();
    }

    // ---------- Mapeadores entidade -> DTO ----------
    // As cópias para ArrayList forçam a inicialização das coleções LAZY dentro da
    // transação e as desacoplam do proxy do Hibernate, evitando o
    // LazyInitializationException na serialização JSON (open-in-view=false).

    private PatientDTO toPatientDTO(Paciente p) {
        return new PatientDTO(
                p.getId(),
                p.getNome(),
                p.getDataNascimento(),
                p.getEndereco(),
                p.getTelefone(),
                p.getPesoCorporal(),
                p.getAltura(),
                p.getNivelAtividadeFisica(),
                p.getEstadoCivil(),
                p.getOcupacao(),
                p.getDataDiagnostiCancer(),
                copy(p.getProcedimentos()),
                copy(p.getAlteracoesCutaneas()),
                p.getQueixasMusculoesqueleticas(),
                p.getSintomasLinfedema(),
                p.getSinalCacifo(),
                p.getSinalCascaLaranja(),
                p.getSinalStemmer(),
                p.getRadiotherapyDTO(),
                p.getSurgeryDTO(),
                p.getAxillaryDissectionDTO(),
                p.getHormonoterapyDTO(),
                p.getDetalhesHormonoterapia(),
                p.getQuimioterapyDTO(),
                p.getObservacaoPaciente()
        );
    }

    private MeasurementResponseDTO toMeasurementDTO(DadosMensuracao d) {
        return new MeasurementResponseDTO(
                d.getId(),
                d.getDataAvaliacao(),
                d.getReferenceArm(),
                d.getAffectedArm(),
                copy(d.getVolumesReferencia()),
                copy(d.getVolumesAfetado()),
                d.getVolumeDifference(),
                d.getPontosRef(),
                copy(d.getLeftArmInputs()),
                copy(d.getRightArmInputs()),
                d.getLeftArmComprimento(),
                d.getRightArmComprimento(),
                copy(d.getDifferences()),
                d.getTipoReferencia(),
                d.getObservacaoMedicao()
        );
    }

    private <T> List<T> copy(List<T> source) {
        return source != null ? new ArrayList<>(source) : new ArrayList<>();
    }
}
