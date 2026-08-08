package com.team42.shared.dto;

public record AuthResponse(
    String token,
    String tokenType,
    Long id,
    String name,
    String email,
    String role
) {
    public AuthResponse(String token, Long id, String name, String email, String role) {
        this(token, "Bearer", id, name, email, role);
    }
}
