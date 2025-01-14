package com.example.demo.util;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Autowired
    private UserRepo userRepo;

    public static final String SECRET = "dsfoisdjiofjsiofjesfei9jfiejuf9ew9i-0fgi0njsiofj";
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 24 hours
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean validateToken(String token, String userEmail) {
        Optional<User> user = userRepo.findByEmail(userEmail);
        if (user.isEmpty()) {
            return false;
        }

        return !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public Claims extractAllClaims(String token) {
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token is expired");
        } catch (JwtException e) {
            throw new RuntimeException("Token is invalid");
        }
    }
}
