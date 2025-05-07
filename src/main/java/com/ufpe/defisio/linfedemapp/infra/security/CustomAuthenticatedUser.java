package com.ufpe.defisio.linfedemapp.infra.security;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomAuthenticatedUser {
    private UUID id;
    private String email;
    private String name;
    private String telefone;
    private String titulacao;
    private String idade;
    private String origem;
}