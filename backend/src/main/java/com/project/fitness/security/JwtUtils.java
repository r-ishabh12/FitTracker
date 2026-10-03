package com.project.fitness.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationsMs;

    public String getJwtFromHeader(HttpServletRequest request){
        if (request.getCookies() != null) {
            for (var cookie : request.getCookies()) {
                if ("FITTRACKER_AUTH".equals(cookie.getName())) return cookie.getValue();
            }
        }
        return null;
    }
    public String generateToken(String userId, String role){

        return Jwts.builder()
                .subject(userId)
                .claim("roles", List.of("ROLE_" + role))
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationsMs))
                .signWith(key())
                .compact();
    }
    public boolean validateJwtToken(String jwtToken){
        try{
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(jwtToken);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserIdFromToken(String jwt) {
        return Jwts.parser().verifyWith((SecretKey) key())
                .build().parseSignedClaims(jwt)
                .getPayload().getSubject();
    }

    public Claims getAllClaims(String jwt) {
        return Jwts.parser().verifyWith((SecretKey) key())
                .build().parseSignedClaims(jwt).getPayload();
    }
}
