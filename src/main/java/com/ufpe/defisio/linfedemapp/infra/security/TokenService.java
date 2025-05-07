package com.ufpe.defisio.linfedemapp.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ufpe.defisio.linfedemapp.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.token}")
    private String token;

    private static final long ACCESS_TOKEN_EXPIRATION = 30 * 24; // 30 dias em horas

    public String generateToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(token);
            return JWT.create()
                    .withIssuer("login-auth-api")
                    .withSubject(user.getEmail())
                    .withClaim("id", user.getId().toString())  // Convertendo UUID para String
                    .withClaim("name", user.getName())
                    .withClaim("idade", user.getIdade())
                    .withClaim("telefone", user.getTelefone())
                    .withClaim("origem", user.getOrigem())
                    .withClaim("titulacao", user.getTitulacao())
                    .withExpiresAt(generateExpirationTime(ACCESS_TOKEN_EXPIRATION))
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Erro enquanto autenticava!");
        }
    }

    public DecodedJWT decodeToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(this.token);
            return JWT.require(algorithm)
                    .withIssuer("login-auth-api")
                    .build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token inválido!");
        }
    }

    private Instant generateExpirationTime(long hours) {
        return LocalDateTime.now().plusHours(hours).toInstant(ZoneOffset.of("-03:00"));
    }
}
