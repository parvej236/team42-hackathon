package com.team42.inventory;

public record SeatDto(
    String seatNumber,
    String rowName,
    String status,
    String heldByUserId
) {
    public static SeatDto from(SeatEntity entity) {
        return new SeatDto(
            entity.getSeatNumber(),
            entity.getRowName(),
            entity.getStatus(),
            entity.getHeldByUserId()
        );
    }
}
