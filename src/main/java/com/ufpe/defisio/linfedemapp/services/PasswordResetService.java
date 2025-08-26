package com.ufpe.defisio.linfedemapp.services;

import com.ufpe.defisio.linfedemapp.domain.user.User;
import com.ufpe.defisio.linfedemapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o e-mail fornecido."));

        String token = generateResetToken();
        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(15));

        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    public void resetPassword(String email, String token, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getResetPasswordToken() == null || !user.getResetPasswordToken().equals(token)) {
            throw new RuntimeException("Token de redefinição inválido.");
        }

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de redefinição expirado.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);

        userRepository.save(user);
    }

    public void verifyCode(String email, String token) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (user.getResetPasswordToken() == null || !user.getResetPasswordToken().equals(token)) {
            throw new RuntimeException("Token de redefinição inválido.");
        }

        if (user.getResetPasswordTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token de redefinição expirado.");
        }
    }

    private String generateResetToken() {
        // Gera um código numérico de 6 dígitos
        return String.format("%06d", new Random().nextInt(999999));
    }
}