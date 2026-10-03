package com.project.fitness.controller;

import com.project.fitness.dto.LoginRequest;
import com.project.fitness.dto.RegisterRequest;
import com.project.fitness.dto.UserResponse;
import com.project.fitness.model.User;
import com.project.fitness.security.JwtUtils;
import com.project.fitness.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;
    @Value("${app.auth.cookie-secure}")
    private boolean secureCookie;

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken token) {
        return new CsrfResponse(token.getToken());
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.authenticate(request);
            ResponseCookie cookie = ResponseCookie.from("FITTRACKER_AUTH", jwtUtils.generateToken(user.getId(), user.getRole().name()))
                    .httpOnly(true).secure(secureCookie).sameSite("Lax").path("/")
                    .maxAge(Duration.ofMillis(expirationMs)).build();
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(userService.mapToResponse(user));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie cookie = ResponseCookie.from("FITTRACKER_AUTH", "")
                .httpOnly(true).secure(secureCookie).sameSite("Lax").path("/")
                .maxAge(Duration.ZERO).build();
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return userService.getById(authentication.getName());
    }

    public record CsrfResponse(String token) {}
}
