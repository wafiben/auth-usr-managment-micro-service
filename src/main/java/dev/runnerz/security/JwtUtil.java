package dev.runnerz.security;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtUtil {


    @Value("${jwt.secret}")
    private String SECRET;

    // Increase to 7 days or 30 days
    private static final long JWT_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7 days

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}