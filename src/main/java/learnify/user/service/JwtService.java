package learnify.user.service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import learnify.user.core.UserEntity;

@Service
public class  JwtService {

    @Value("${app.jwt.key-string}")
    private String keyString;

    private SecretKey getSecretKey() {
        byte[] decoded = Base64.getDecoder().decode(keyString);
        return Keys.hmacShaKeyFor(decoded);
    }

    public String generateJwtToken(UserEntity userEntity) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("name", userEntity.getName());
        claims.put("role", userEntity.getRole());

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .claims(claims)
                .subject(userEntity.getUsername())
                .signWith(getSecretKey())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 2))
                .compact();

    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        // Check: Username match kare AND Token expire na hua ho
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
