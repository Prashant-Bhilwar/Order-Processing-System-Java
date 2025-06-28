package com.prashant.auth.service;

import com.prashant.auth.dto.AuthResponse;
import com.prashant.auth.dto.LoginRequest;
import com.prashant.auth.dto.RegisterRequest;
import com.prashant.auth.entity.User;
import com.prashant.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private  final RefreshTokenService refreshTokenService;

    public AuthResponse register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("User already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER").build();

        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken, jwtService.getRefreshTokenExpiration());
        return new AuthResponse(accessToken,refreshToken);
    }

    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenService.saveRefreshToken(user.getEmail(), refreshToken, jwtService.getRefreshTokenExpiration());
        return new AuthResponse(accessToken,refreshToken);
    }

    public void logout(String email){
        refreshTokenService.deleteRefreshToken(email);
    }

    public AuthResponse refreshToken(String refreshToken){
        String email = jwtService.extractUsername(refreshToken);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Validate the refresh token via JWT and Redis
        if (!jwtService.isTokenValid(refreshToken, user) || !refreshTokenService.isRefreshTokenValid(email, refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        refreshTokenService.saveRefreshToken(email,newRefreshToken, jwtService.getRefreshTokenExpiration());

        return new AuthResponse(newAccessToken, newRefreshToken);
    }
}
