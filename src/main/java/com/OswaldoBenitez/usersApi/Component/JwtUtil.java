
package com.OswaldoBenitez.usersApi.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET = "MySuperSecretJwtKey1234567890AB12";
    private static final long EXPIRATION_MS = 86400000;
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String taxId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + EXPIRATION_MS);

        String token = Jwts.builder()
                .setSubject(taxId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return token;
    }

    public String extractTaxId(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

