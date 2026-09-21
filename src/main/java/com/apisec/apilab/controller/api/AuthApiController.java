package com.apisec.apilab.controller.api;

import com.apisec.apilab.dto.AuthResponse;
import com.apisec.apilab.dto.LoginRequest;
import com.apisec.apilab.dto.ProfileResponse;
import com.apisec.apilab.dto.RegisterRequest;
import com.apisec.apilab.entity.User;
import com.apisec.apilab.security.JwtCookieService;
import com.apisec.apilab.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthApiController {

    private final AuthService authService;
    private final JwtCookieService jwtCookieService;

    public AuthApiController(AuthService authService, JwtCookieService jwtCookieService) {
        this.authService = authService;
        this.jwtCookieService = jwtCookieService;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponse body = authService.register(request);
        jwtCookieService.write(response, body.getToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse body = authService.login(request);
        jwtCookieService.write(response, body.getToken());
        return ResponseEntity.ok(body);
    }

    @GetMapping("/api/profile")
    public ProfileResponse profile(@AuthenticationPrincipal User user) {
        return authService.toProfile(user);
    }
}
