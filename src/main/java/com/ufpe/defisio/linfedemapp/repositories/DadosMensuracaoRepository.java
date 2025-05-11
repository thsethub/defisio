package com.ufpe.defisio.linfedemapp.repositories;

import com.ufpe.defisio.linfedemapp.domain.paciente.DadosMensuracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DadosMensuracaoRepository extends JpaRepository<DadosMensuracao, UUID> {
    Optional<DadosMensuracao> findByPacienteId(UUID pacienteId);
    List<DadosMensuracao> findAllByPacienteId(UUID pacienteId);
    void deleteAllByPacienteId(UUID pacienteId);
}
