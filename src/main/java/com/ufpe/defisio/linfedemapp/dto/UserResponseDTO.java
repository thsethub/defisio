package com.ufpe.defisio.linfedemapp.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String idade,
        String telefone,
        String origem,
        String titulacao
) {}
