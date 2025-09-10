package com.bankmega.certification.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

public class JwtUtil {
    private static final String SECRET = "B4nKMegaGantengP4keJwTSecretKey123!"; // Min 32 chars
    private static final long EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 jam

    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static String getRoleFromToken(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private static boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public static String generateToken(String username, String roleName) {
        return Jwts.builder()
                .subject(username)
                .claim("role", roleName)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    public static boolean validateToken(String token, String username) {
        try {
            final String usernameInToken = getUsernameFromToken(token);
            return (usernameInToken.equals(username) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            System.err.println("Invalid JWT Token: " + e.getMessage());
            return false;
        }
    }
}