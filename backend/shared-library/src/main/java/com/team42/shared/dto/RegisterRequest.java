package com.team42.shared.dto;

public record RegisterRequest(
    String name,
    String email,
    String password,
    String role
) {}
