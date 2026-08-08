package com.team42.auth.service;

import com.team42.auth.model.UserEntity;
import com.team42.auth.repository.UserRepository;
import com.team42.shared.dto.*;
import com.team42.shared.security.JwtUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @org.springframework.beans.factory.annotation.Value("${services.user-service.url:http://localhost:8085}")
    private String userServiceUrl;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered: " + request.email());
        }

        UserEntity user = new UserEntity();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role() != null ? request.role() : "ROLE_USER");

        UserEntity saved = userRepository.save(user);

        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            java.util.Map<String, Object> userMap = new java.util.HashMap<>();
            userMap.put("name", saved.getName());
            userMap.put("email", saved.getEmail());
            userMap.put("role", saved.getRole());
            userMap.put("provider", "LOCAL");
            restTemplate.postForEntity(userServiceUrl + "/api/v1/users", userMap, Object.class);
        } catch (Exception e) {
            System.err.println("Failed to sync user to user-service: " + e.getMessage());
        }

        String token = jwtUtils.generateToken(saved.getEmail(), saved.getRole(), saved.getId());
        return new AuthResponse(token, saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(AuthRequest request) {
        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtils.generateToken(user.getEmail(), user.getRole(), user.getId());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
