package com.diplomado.billing_app.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;

public class JwtUtil {

    // VULNERABILIDAD CRÍTICA A02 (Fallos Criptográficos):
    // 1. Clave secreta quemada (hardcoded) en el código fuente.
    // 2. Clave demasiado débil y predecible.
    // SonarQube catalogará esto como Blocker.
    public static final String SECRET_KEY = "BillingSecSuperSecretKey2026";

    public static String generateToken(String username, Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("userId", userId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 horas
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Long getUserIdFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().get("userId", Long.class);
        } catch (Exception e) {
            return null; // Token inválido
        }
    }
}