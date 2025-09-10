package edu.unam.springsecurity.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import edu.unam.springsecurity.security.UserDetailsImpl;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {

    private final Key jwtSecretKey;
    private final long jwtExpirationMs;

    public JwtTokenUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expirationDateInMs}") long expirationMs
    ) {
        this.jwtSecretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationMs = expirationMs * 1000; // segundos a milisegundos
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        var roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Integer uid = null;
        if (authentication.getPrincipal() instanceof UserDetailsImpl udi) {
            uid = udi.getId();
        }

        return Jwts.builder()
                .setSubject(username)
                .claim("uid", uid)
                .claim("roles", roles)
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getBody().getSubject();
    }

    public String getRolesFromToken(String token) {
        return parseClaims(token).getBody().get("roles").toString();
    }

    public Integer getUserIdFromToken(String token) {
        Object uid = parseClaims(token).getBody().get("uid");
        if (uid == null) return null;
        if (uid instanceof Integer i) return i;
        if (uid instanceof Number n) return n.intValue();
        return Integer.valueOf(uid.toString());
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("❌ Token inválido: " + e.getMessage());
            return false;
        }
    }

    private Jws<Claims> parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token);
    }
}
