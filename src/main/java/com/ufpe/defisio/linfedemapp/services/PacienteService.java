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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final DadosMensuracaoRepository dadosMensuracaoRepository;
    private final UserRepository userRepository;

    public List<Paciente> listarPacientesPorUsuario(UUID usuarioId) {
        return pacienteRepository.findByUsuarioId(usuarioId);
    }

    public Paciente buscarPacientePorId(UUID pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public void deletarPaciente(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        pacienteRepository.delete(paciente); // remove em cascata
    }

    // Metodo para adicionar um novo paciente
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
        dados.setDataAvaliacao(LocalDate.now()); // Define a data da requisição
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

    // Metodo para retornar paciente com as medições
    public PacienteComMensuracaoResponseDTO getPacienteComMensuracao(UUID pacienteId) {
        // Buscar paciente
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        // Buscar dados de mensuração
        Optional<DadosMensuracao> dadosMensuracao = dadosMensuracaoRepository.findByPacienteId(pacienteId);

        // Criar DTO de Medições
        MeasurementsDTO measurementsDTO = new MeasurementsDTO();
        dadosMensuracao.ifPresent(dm -> {
            // Preenchendo as medições com os dados de volumetria e perimetria
            measurementsDTO.setVolumetry(dm.getVolumetryDTO());
            measurementsDTO.setPerimetry(dm.getPerimetryDTO());
        });

        // Criar DTO do Paciente
        PatientDTO patientDTO = new PatientDTO(
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
                paciente.getProcedimentos(), // Lista de procedimentos
                paciente.getAlteracoesCutaneas(), // Lista de alterações cutâneas
                paciente.getQueixasMusculoesqueleticas(),
                paciente.getSintomasLinfedema(),
                paciente.getSinalCacifo(),
                paciente.getSinalCascaLaranja(),
                paciente.getSinalStemmer(),
                paciente.getRadiotherapyDTO(),  // Usando o DTO de radioterapia
                paciente.getSurgeryDTO(),      // Usando o DTO de cirurgia
                paciente.getAxillaryDissectionDTO()  // Usando o DTO de dissecção axilar
        );

        // Retornar o DTO com Paciente e Medições
        return new PacienteComMensuracaoResponseDTO(patientDTO, measurementsDTO);
    }

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
                paciente.getProcedimentos(),
                paciente.getAlteracoesCutaneas(),
                paciente.getQueixasMusculoesqueleticas(),
                paciente.getSintomasLinfedema(),
                paciente.getSinalCacifo(),
                paciente.getSinalCascaLaranja(),
                paciente.getSinalStemmer(),
                paciente.getRadiotherapyDTO(),
                paciente.getSurgeryDTO(),
                paciente.getAxillaryDissectionDTO()
        )).toList();
    }

    public List<DadosMensuracao> listarMensuracoesPorPaciente(UUID pacienteId, UUID usuarioId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (!paciente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Este paciente não pertence ao usuário informado");
        }

        return dadosMensuracaoRepository.findAllByPacienteId(pacienteId);
    }
}
