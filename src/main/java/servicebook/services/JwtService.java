package servicebook.services;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;

import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import servicebook.user.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    /**
     * Генерація токена
     *
     * @param user дані користувача
     * @return токен
     */
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole());

        return generateToken(claims, user);
    }

    /**
     * Перевірка токена на валідність
     *
     * @param token токен
     * @param user дані користувача
     * @return true, якщо токен пройшов валідацію
     */
    public boolean isTokenValid(String token, User user) {
        try {
            if (user == null)
                return false;

            SecretKey key = getSecretKey();

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            Date expiration = claims.getExpiration();

            return email.equals(user.getEmail()) && !expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Генерація токена
     *
     * @param extraClaims додаткові дані
     * @param user дані користувача
     * @return токен
     */
    private String generateToken(Map<String, Object> extraClaims, User user) {
        SecretKey key = getSecretKey();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(key)
                .compact();
    }

    /**
     * Отримання ключа для підпису токена
     *
     * @return ключ
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}