package com.ufpe.defisio.linfedemapp.domain.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String name;
    private String idade;
    private String origem;
    private String email;
    private String telefone;
    private String titulacao;
    private String password;

    //Redefinição de Senha
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenExpiry;

}
