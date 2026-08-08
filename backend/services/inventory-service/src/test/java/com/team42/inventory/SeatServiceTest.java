package com.team42.inventory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHoldSeatSuccess() {
        SeatEntity seat = new SeatEntity(1L, "F12", "F");
        seat.setStatus("AVAILABLE");

        when(seatRepository.findByShowtimeIdAndSeatNumberForUpdate(1L, "F12"))
                .thenReturn(Optional.of(seat));

        Map<String, Object> response = seatService.holdSeat(1L, "F12", "user@test.com");

        assertTrue((Boolean) response.get("success"));
        assertEquals("HELD", seat.getStatus());
        assertEquals("user@test.com", seat.getHeldByUserId());
        verify(seatRepository, times(1)).save(seat);
    }

    @Test
    void testHoldSeatAlreadyHeldByOtherUserThrowsException() {
        SeatEntity seat = new SeatEntity(1L, "F12", "F");
        seat.setStatus("HELD");
        seat.setHeldByUserId("other@test.com");

        when(seatRepository.findByShowtimeIdAndSeatNumberForUpdate(1L, "F12"))
                .thenReturn(Optional.of(seat));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            seatService.holdSeat(1L, "F12", "user@test.com");
        });

        assertTrue(exception.getMessage().contains("not available"));
    }

    @Test
    void testReleaseExpiredHolds() {
        when(seatRepository.releaseExpiredHolds(any(LocalDateTime.class))).thenReturn(3);

        seatService.releaseExpiredHolds();

        verify(seatRepository, times(1)).releaseExpiredHolds(any(LocalDateTime.class));
    }
}
