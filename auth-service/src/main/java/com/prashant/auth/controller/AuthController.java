package com.prashant.auth.controller;

import com.prashant.auth.dto.AuthResponse;
import com.prashant.auth.dto.LoginRequest;
import com.prashant.auth.dto.RegisterRequest;
import com.prashant.auth.service.AuthService;
import com.prashant.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "Auth API for login, register, logout, refresh")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @Operation(summary = "Register a new user")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register (@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration request body",
            required = true,
            content = @Content(schema = @Schema(implementation = RegisterRequest.class))
    ) @RequestBody @Valid RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(summary = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User login request body",
            required = true,
            content = @Content(schema = @Schema(implementation = LoginRequest.class))
    ) @RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Logout user and revoke refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Missing or malformed Authorization header")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @Parameter(description = "JWT access token in 'Authorization' header", example = "Bearer eyJhbGciOiJIUzI1NiiIsInR...")
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        authService.logout(email, token);

        return ResponseEntity.ok("Logged out successfully");
    }

    @Operation(summary = "Refresh access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid refresh token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Refresh token as JSON: { \"refreshToken\": \"...\"}",
            required = true,
            content = @Content(schema = @Schema(example = "{\"refreshToken\": \"eyJhnGciOi...\"}"))
    ) @RequestBody Map<String,String> request) {
        String refreshToken = request.get("refreshToken");

        if(refreshToken == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}
