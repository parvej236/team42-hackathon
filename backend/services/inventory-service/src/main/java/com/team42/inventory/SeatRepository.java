package com.team42.inventory;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<SeatEntity, Long> {

    List<SeatEntity> findByShowtimeIdOrderByIdAsc(Long showtimeId);

    /**
     * Pessimistic write lock - prevents double-booking under concurrency.
     * SELECT ... FOR UPDATE on the specific seat row.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SeatEntity s WHERE s.showtimeId = :showtimeId AND s.seatNumber = :seatNumber")
    Optional<SeatEntity> findByShowtimeIdAndSeatNumberForUpdate(
        @Param("showtimeId") Long showtimeId,
        @Param("seatNumber") String seatNumber
    );

    Optional<SeatEntity> findByShowtimeIdAndSeatNumber(Long showtimeId, String seatNumber);

    /**
     * Find all expired holds that need to be released.
     */
    @Query("SELECT s FROM SeatEntity s WHERE s.status = 'HELD' AND s.heldAt < :expiry")
    List<SeatEntity> findExpiredHolds(@Param("expiry") LocalDateTime expiry);

    /**
     * Bulk release expired holds.
     */
    @Modifying
    @Query("UPDATE SeatEntity s SET s.status = 'AVAILABLE', s.heldByUserId = null, s.heldAt = null WHERE s.status = 'HELD' AND s.heldAt < :expiry")
    int releaseExpiredHolds(@Param("expiry") LocalDateTime expiry);

    long countByShowtimeIdAndSeatNumberAndStatus(Long showtimeId, String seatNumber, String status);
}
