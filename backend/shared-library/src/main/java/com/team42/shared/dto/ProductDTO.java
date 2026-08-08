package com.team42.shared.dto;

import java.math.BigDecimal;

public record ProductDTO(
    Long id,
    String name,
    String category,
    BigDecimal price,
    BigDecimal originalPrice,
    String discount,
    Double rating,
    Integer reviews,
    String image,
    String description,
    Boolean isFlashSale,
    Integer stockLeft
) {}
