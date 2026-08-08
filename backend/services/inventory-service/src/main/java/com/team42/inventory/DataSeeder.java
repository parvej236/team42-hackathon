package com.team42.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedSeats(SeatService seatService) {
        return args -> {
            // Pre-populate seats for the first few showtimes
            // Showtimes IDs 1-48 are for Spider-Man across 2 theatres x 2 halls x 12 slots
            for (long showtimeId = 1; showtimeId <= 6; showtimeId++) {
                seatService.initializeSeatsForShowtime(showtimeId, 13, 14);
            }
            System.out.println("✅ Inventory: Pre-initialized seats for first 6 showtimes (13 rows x 14 seats = 182 seats each)");
        };
    }
}
