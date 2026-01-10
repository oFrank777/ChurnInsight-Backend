package com.alura.churninsight.Security;

import com.alura.churninsight.domain.Usuario.Usuario;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer("API ChurnInsight")
                .withSubject(usuario.getUsername())
                .withExpiresAt(LocalDateTime.now()
                        .plusHours(8)
                        .toInstant(ZoneOffset.of("-05:00")))
                .sign(Algorithm.HMAC256(secret));
    }

    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("API ChurnInsight")
                .build()
                .verify(token)
                .getSubject();
    }
}
