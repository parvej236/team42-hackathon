package com.team42.shared.dto;

import java.io.Serializable;

public record UserDTO(
    Long id,
    String name,
    String email,
    String avatar,
    String role,
    String provider
) implements Serializable {}
