package com.ufpe.defisio.linfedemapp.controllers;


import com.ufpe.defisio.linfedemapp.domain.user.User;
import com.ufpe.defisio.linfedemapp.dto.LoginRequestDTO;
import com.ufpe.defisio.linfedemapp.dto.RegisterRequestDTO;
import com.ufpe.defisio.linfedemapp.dto.ResponseDTO;
import com.ufpe.defisio.linfedemapp.dto.UserResponseDTO;
import com.ufpe.defisio.linfedemapp.infra.security.CustomAuthenticatedUser;
import com.ufpe.defisio.linfedemapp.infra.security.TokenService;
import com.ufpe.defisio.linfedemapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {
        User user = this.repository.findByEmail(body.email()).orElseThrow(() -> new RuntimeException("Usário não encontrado!"));
        if (passwordEncoder.matches(body.password(), user.getPassword())) {
            String chave = this.tokenService.generateToken(user);
            return ResponseEntity.ok(new ResponseDTO(user.getEmail(), chave));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
        Optional<User> user = this.repository.findByEmail(body.email());

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            newUser.setIdade(body.idade());
            newUser.setTelefone(body.telefone());
            newUser.setOrigem(body.origem());
            newUser.setTitulacao(body.titulacao());
            this.repository.save(newUser);
            String chave = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getEmail(), chave));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomAuthenticatedUser authenticatedUser = (CustomAuthenticatedUser) authentication.getPrincipal();

        // Verifica se o usuário ainda existe no banco
        Optional<User> userOptional = repository.findById(authenticatedUser.getId());
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Usuário não encontrado");
        }

        User user = userOptional.get();
        return ResponseEntity.ok(new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getIdade(),
                user.getTelefone(),
                user.getOrigem(),
                user.getTitulacao()
        ));
    }
}
