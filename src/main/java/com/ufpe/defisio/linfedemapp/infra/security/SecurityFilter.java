package com.ufpe.defisio.linfedemapp.infra.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recoverToken(request);

        if (token != null) {
            try {
                DecodedJWT jwt = tokenService.decodeToken(token);

                UUID id = UUID.fromString(jwt.getClaim("id").asString());  // Convertendo a String para UUID
                String email = jwt.getSubject();
                String name = jwt.getClaim("name").asString();
                String telefone = jwt.getClaim("telefone").asString();
                String titulacao = jwt.getClaim("titulacao").asString();
                String idade = jwt.getClaim("idade").asString();
                String origem = jwt.getClaim("origem").asString();

                // Você pode criar um objeto User customizado ou só salvar no contexto a identificação
                CustomAuthenticatedUser principal = new CustomAuthenticatedUser(
                       id, email, name, telefone, titulacao, idade, origem
                );

                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.replace("Bearer ", "");
    }
}
