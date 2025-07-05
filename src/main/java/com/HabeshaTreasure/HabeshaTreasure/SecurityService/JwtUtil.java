package com.HabeshaTreasure.HabeshaTreasure.SecurityService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    // Secret key used to sign the JWT token
    @Value("${jwt.secret}")
    private String secretKeyString;

    private SecretKey SECRET_KEY;

    @PostConstruct
    public void init() {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }
    // Token validity duration in milliseconds (e.g., 1 hour)
    private final long TOKEN_VALIDITY = 5 * 60 * 60 * 1000;



    public String generateSECRET_KEY(int byteLength){
        SecureRandom secureRandom = new SecureRandom();
        byte[] keyBytes = new byte[byteLength];
        secureRandom.nextBytes(keyBytes);
        System.out.println("Generated secret key: " + Base64.getEncoder().encodeToString(keyBytes));
        return Base64.getEncoder().encodeToString(keyBytes);
    }


    /**
     * Generate a JWT token for the given user.
     *
     * @param userDetails The UserDetails object containing user information.
     * @return The generated JWT token as a string.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        System.out.println("Generating token for user: " + userDetails.getUsername());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Create a JWT token with the specified claims and subject.
     *
     * @param claims  Additional claims to include in the token.
     * @param subject The subject of the token (e.g., user's email).
     * @return The generated JWT token as a string.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        System.out.println("Creating token for subject: " + subject);
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    /**
     * Validate a JWT token against the UserDetails.
     *
     * @param token       The JWT token to validate.
     * @param userDetails The UserDetails object containing user information.
     * @return True if the token is valid, false otherwise.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Extract the email (subject) from the JWT token.
     *
     * @param token The JWT token.
     * @return The email extracted from the token.
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Check if the JWT token has expired.
     *
     * @param token The JWT token.
     * @return True if the token has expired, false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Helper method to extract a specific claim from the JWT token.
     *
     * @param token          The JWT token.
     * @param claimsResolver A function to resolve the desired claim.
     * @param <T>            The type of the claim.
     * @return The extracted claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from the JWT token.
     *
     * @param token The JWT token.
     * @return The Claims object containing all claims.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

