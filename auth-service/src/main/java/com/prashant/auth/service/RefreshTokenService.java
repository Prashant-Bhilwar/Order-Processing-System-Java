package com.prashant.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    public void saveRefreshToken(String email, String refreshToken, long expirationMillis){
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + email,
                refreshToken,
                expirationMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public boolean isRefreshTokenValid(String email, String refreshToken){
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public void deleteRefreshToken(String email, String token) {
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + email);
        if(!token.equals(storedToken)){
            throw new RuntimeException("Invalid or outdated token");
        }
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + email);
    }
}
