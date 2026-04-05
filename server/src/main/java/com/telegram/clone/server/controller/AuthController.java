package com.telegram.clone.server.controller;

import com.telegram.clone.common.dto.AuthRequestDto;
import com.telegram.clone.common.dto.AuthResponseDto;
import com.telegram.clone.server.dto.request.LoginRequest;
import com.telegram.clone.server.dto.response.LoginResponse;
import com.telegram.clone.server.service.impl.AuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для аутентификации пользователей")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns session token")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for user: {}", request.username());

        AuthRequestDto authRequest = new AuthRequestDto(request.username(), request.password());
        AuthResponseDto response = authService.authenticate(authRequest);

        if (response.success()) {
            return LoginResponse.success(
                response.username(),
                response.displayName(),
                response.token()
            );
        } else {
            return LoginResponse.error(response.message());
        }
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate token", description = "Checks if session token is valid")
    public boolean validateToken(@RequestParam String token) {
        return authService.validateToken(token);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates user session")
    public void logout(@RequestParam String token) {
        String username = authService.getUsernameByToken(token);
        if (username != null) {
            authService.invalidateSession(username);
            log.info("User logged out: {}", username);
        }
    }
}