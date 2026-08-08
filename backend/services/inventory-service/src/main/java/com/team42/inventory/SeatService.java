package com.team42.inventory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    @Value("${app.hold-ttl-seconds:60}")
    private int holdTtlSeconds;

    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    /**
     * Get the real-time seat map for a showtime.
     */
    public List<SeatDto> getSeatMap(Long showtimeId) {
        return seatRepository.findByShowtimeIdOrderByIdAsc(showtimeId)
                .stream()
                .map(SeatDto::from)
                .collect(Collectors.toList());
    }

    /**
     * Hold a seat with pessimistic locking to prevent double-booking.
     * This is the critical concurrency path.
     */
    @Transactional
    public Map<String, Object> holdSeat(Long showtimeId, String seatNumber, String userId) {
        // Pessimistic lock: SELECT FOR UPDATE
        SeatEntity seat = seatRepository.findByShowtimeIdAndSeatNumberForUpdate(showtimeId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found: " + seatNumber));

        // Check if already held by this user
        if ("HELD".equals(seat.getStatus()) && seat.getHeldByUserId() != null && userId.equalsIgnoreCase(seat.getHeldByUserId())) {
            return Map.of("success", true, "message", "Seat already held by you", "seatNumber", seatNumber);
        }

        // Check if seat is available
        if (!"AVAILABLE".equals(seat.getStatus())) {
            throw new RuntimeException("Seat " + seatNumber + " is not available (status: " + seat.getStatus() + ")");
        }

        // Hold the seat
        seat.setStatus("HELD");
        seat.setHeldByUserId(userId);
        seat.setHeldAt(LocalDateTime.now());
        seatRepository.save(seat);

        return Map.of(
            "success", true,
            "message", "Seat held successfully",
            "seatNumber", seatNumber,
            "holdExpiresInSeconds", holdTtlSeconds
        );
    }

    /**
     * Release a held seat back to available.
     */
    @Transactional
    public Map<String, Object> releaseSeat(Long showtimeId, String seatNumber, String userId) {
        SeatEntity seat = seatRepository.findByShowtimeIdAndSeatNumberForUpdate(showtimeId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found: " + seatNumber));

        if (!"HELD".equals(seat.getStatus()) || !userId.equals(seat.getHeldByUserId())) {
            throw new RuntimeException("Cannot release seat " + seatNumber + ": not held by you");
        }

        seat.setStatus("AVAILABLE");
        seat.setHeldByUserId(null);
        seat.setHeldAt(null);
        seatRepository.save(seat);

        return Map.of("success", true, "message", "Seat released", "seatNumber", seatNumber);
    }

    /**
     * Confirm a held seat (after payment succeeds).
     */
    @Transactional
    public Map<String, Object> confirmSeat(Long showtimeId, String seatNumber, String userId) {
        SeatEntity seat = seatRepository.findByShowtimeIdAndSeatNumberForUpdate(showtimeId, seatNumber)
                .orElseThrow(() -> new RuntimeException("Seat not found: " + seatNumber));

        if ("BOOKED".equals(seat.getStatus())) {
            return Map.of("success", true, "message", "Seat already confirmed", "seatNumber", seatNumber);
        }

        seat.setStatus("BOOKED");
        seat.setHeldByUserId(null);
        seat.setBookedAt(LocalDateTime.now());
        seatRepository.save(seat);

        return Map.of("success", true, "message", "Seat confirmed/booked", "seatNumber", seatNumber);
    }

    /**
     * Scheduled task: automatically release expired holds.
     * Runs every 5 seconds to check for holds that exceed HOLD_TTL_SECONDS.
     */
    @Scheduled(fixedRate = 1000)
    @Transactional
    public void releaseExpiredHolds() {
        LocalDateTime expiry = LocalDateTime.now().minusSeconds(holdTtlSeconds);
        int released = seatRepository.releaseExpiredHolds(expiry);
        if (released > 0) {
            System.out.println("🔄 Auto-released " + released + " expired seat hold(s) (TTL=" + holdTtlSeconds + "s)");
        }
    }

    /**
     * Initialize seats for a showtime if they don't exist yet.
     */
    @Transactional
    public void initializeSeatsForShowtime(Long showtimeId, int totalRows, int seatsPerRow) {
        if (!seatRepository.findByShowtimeIdOrderByIdAsc(showtimeId).isEmpty()) {
            return; // Already initialized
        }

        String[] rowLabels = {"A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N"};
        int rowCount = Math.min(totalRows, rowLabels.length);

        for (int r = 0; r < rowCount; r++) {
            for (int c = 1; c <= seatsPerRow; c++) {
                String seatNumber = rowLabels[r] + c;
                seatRepository.save(new SeatEntity(showtimeId, seatNumber, rowLabels[r]));
            }
        }
    }
}
