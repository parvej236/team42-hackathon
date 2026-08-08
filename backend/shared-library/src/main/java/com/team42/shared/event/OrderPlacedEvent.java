package com.team42.shared.event;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderPlacedEvent(
    Long orderId,
    String userEmail,
    BigDecimal totalAmount,
    Integer totalItems,
    String status,
    LocalDateTime timestamp
) implements Serializable {}
