package com.team42.shared.dto;

public record AuthRequest(
    String email,
    String password
) {}
