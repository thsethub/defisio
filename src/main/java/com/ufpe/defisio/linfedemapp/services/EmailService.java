package com.ufpe.defisio.linfedemapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject("Seu Código de Redefinição de Senha");
        message.setText("Olá, \n\nSeu código para redefinição de senha é: " + token
                + "\n\nEste código irá expirar em 15 minutos. \n\nSe você não solicitou esta redefinição, por favor, ignore este e-mail.\n\nAtenciosamente,\n\nLinfedemapp");
        mailSender.send(message);
    }
}
