package com.team42.inventory;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"showtime_id", "seat_number"})
})
public class SeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "showtime_id", nullable = false)
    private Long showtimeId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber;  // e.g. "F12"

    @Column(name = "row_name", nullable = false)
    private String rowName;     // e.g. "F"

    /**
     * AVAILABLE - free to hold
     * HELD     - temporarily held by a user (with TTL)
     * BOOKED   - confirmed and paid
     */
    @Column(nullable = false)
    private String status = "AVAILABLE";

    @Column(name = "held_by_user_id")
    private String heldByUserId;

    @Column(name = "held_at")
    private LocalDateTime heldAt;

    @Column(name = "booked_at")
    private LocalDateTime bookedAt;

    @Version
    private Long version;

    public SeatEntity() {}

    public SeatEntity(Long showtimeId, String seatNumber, String rowName) {
        this.showtimeId = showtimeId;
        this.seatNumber = seatNumber;
        this.rowName = rowName;
        this.status = "AVAILABLE";
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getShowtimeId() { return showtimeId; }
    public void setShowtimeId(Long showtimeId) { this.showtimeId = showtimeId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public String getRowName() { return rowName; }
    public void setRowName(String rowName) { this.rowName = rowName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getHeldByUserId() { return heldByUserId; }
    public void setHeldByUserId(String heldByUserId) { this.heldByUserId = heldByUserId; }
    public LocalDateTime getHeldAt() { return heldAt; }
    public void setHeldAt(LocalDateTime heldAt) { this.heldAt = heldAt; }
    public LocalDateTime getBookedAt() { return bookedAt; }
    public void setBookedAt(LocalDateTime bookedAt) { this.bookedAt = bookedAt; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
