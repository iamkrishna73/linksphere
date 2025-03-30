package com.codewithme.linksphere.authentication.security.jwt;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Extracts the JWT token from the Authorization header
     */
    public String getJwtTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.debug("Authorization header received: {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    /**
     * Generates JWT Token for a given user (email-based login)
     */
    public String generateTokenFromUserEmail(String email) {
       // String email = userDetails.getUsername(); // username is actually email
        return Jwts.builder()
                .subject(email)  // "sub" claim should be the email
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(key())  // Use correct signing algorithm
                .compact();
    }

    /**
     * Extracts the email (username) from a JWT token
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();  // Get subject which contains email
    }

    /**
     * Validates the JWT token
     */
    public boolean validateToken(String authToken) {
        try {
            logger.debug("Validating JWT token...");
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Returns the signing key for JWT
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret)); // Ensure jwtSecret is properly encoded
    }
}
