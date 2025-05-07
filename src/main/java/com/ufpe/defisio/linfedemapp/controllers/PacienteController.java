package com.ufpe.defisio.linfedemapp.controllers;

import com.ufpe.defisio.linfedemapp.domain.paciente.DadosMensuracao;
import com.ufpe.defisio.linfedemapp.domain.paciente.Paciente;
import com.ufpe.defisio.linfedemapp.dto.Paciente.MeasurementsDTO;
import com.ufpe.defisio.linfedemapp.dto.Paciente.PacienteRequestDTO;
import com.ufpe.defisio.linfedemapp.dto.Paciente.PacienteComMensuracaoResponseDTO;
import com.ufpe.defisio.linfedemapp.dto.Paciente.PatientDTO;
import com.ufpe.defisio.linfedemapp.repositories.DadosMensuracaoRepository;
import com.ufpe.defisio.linfedemapp.repositories.PacienteRepository;
import com.ufpe.defisio.linfedemapp.services.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @PostMapping
    public ResponseEntity<Paciente> addPaciente(@RequestBody PacienteRequestDTO dto) {
        Paciente novoPaciente = pacienteService.addPaciente(dto);
        return ResponseEntity.ok(novoPaciente);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<PatientDTO>> listarPacientesPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(pacienteService.listarPacientesDTO(usuarioId));
    }

    @GetMapping("/usuario/{usuarioId}/{pacienteId}/mensuracoes")
    public ResponseEntity<List<DadosMensuracao>> listarMensuracoesPorPaciente(
            @PathVariable UUID usuarioId,
            @PathVariable UUID pacienteId
    ) {
        return ResponseEntity.ok(pacienteService.listarMensuracoesPorPaciente(pacienteId, usuarioId));
    }

    @GetMapping("/{pacienteId}")
    public ResponseEntity<Paciente> buscarPacientePorId(@PathVariable UUID pacienteId) {
        Paciente paciente = pacienteService.buscarPacientePorId(pacienteId);
        return ResponseEntity.ok(paciente);
    }

    @DeleteMapping("/{pacienteId}")
    public ResponseEntity<Void> deletarPaciente(@PathVariable UUID pacienteId) {
        pacienteService.deletarPaciente(pacienteId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{pacienteId}/mensuracao")
    public ResponseEntity<?> adicionarMensuracao(@PathVariable UUID pacienteId, @RequestBody MeasurementsDTO measurementsDTO) {
        var dadosSalvos = pacienteService.addDadosMensuracao(pacienteId, measurementsDTO);
        return ResponseEntity.ok(dadosSalvos);
    }

}
