package com.ufpe.defisio.linfedemapp.repositories;

import com.ufpe.defisio.linfedemapp.domain.paciente.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
    Optional<Paciente> findById(UUID id);
    List<Paciente> findByUsuarioId(UUID usuarioId);

}
