package com.prashant.auth.controller;

import com.prashant.auth.config.AppConfig;
import com.prashant.auth.config.SecurityConfig;
import com.prashant.auth.dto.AuthResponse;
import com.prashant.auth.dto.LoginRequest;
import com.prashant.auth.dto.RegisterRequest;
import com.prashant.auth.repository.UserRepository;
import com.prashant.auth.service.AuthService;
import com.prashant.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({JwtService.class, SecurityConfig.class, AppConfig.class})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("accessToken123", "refreshToken456");

        Mockito.when(authService.register(Mockito.any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                         "email": "test@example.com",
                         "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessToken123"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken456"));
    }

    @Test
    void testLogin() throws Exception{
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        AuthResponse response = new AuthResponse("accessTokenXYZ", "refreshTokenABC");

        Mockito.when(authService.login(Mockito.any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                         {
                            "email": "test@example.com",
                            "password": "password123"
                         }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("accessTokenXYZ"))
                .andExpect(jsonPath("$.refreshToken").value("refreshTokenABC"));
    }
}
