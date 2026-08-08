package com.team42.product;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedMoviesAndShowtimes(MovieRepository movieRepo, ShowtimeRepository showtimeRepo) {
        return args -> {
            if (movieRepo.count() > 0) return;

            // ===== MOVIES =====
            MovieEntity spiderman = new MovieEntity(
                "Spider-Man: Brand New Day",
                "2D / 3D",
                "Now Showing",
                "July 31, 2026",
                "https://images.unsplash.com/photo-1635805737707-575885ab0820?w=600&auto=format&fit=crop&q=80",
                "Four years have gone by since we last caught up with our friendly neighborhood hero. Peter Parker is no more, but Spider-Man is at the top of his game keeping New York City safe. Things are going well for our anonymous hero until an unusual trail of crimes pulls him into a web of mystery larger than he's ever faced before.",
                "TOM HOLLAND, ZENDAYA, SADIE SINK, LIZA COLÓN, JACOB BATALON",
                "Action, Adventure, Sci-Fi",
                "English",
                "2h 24m"
            );

            MovieEntity odyssey = new MovieEntity(
                "The Odyssey",
                "2D",
                "Now Showing",
                "July 17, 2026",
                "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=600&auto=format&fit=crop&q=80",
                "An epic tale of adventure and survival on ancient seas, bringing Homer's classic to life with stunning visuals.",
                "BRAD PITT, MARGOT ROBBIE",
                "Adventure, Drama",
                "English",
                "2h 38m"
            );

            MovieEntity fastX = new MovieEntity(
                "Fast X: Part II",
                "2D / IMAX",
                "Coming Soon",
                "August 28, 2026",
                "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=600&auto=format&fit=crop&q=80",
                "The final ride of the saga continues at top speed. Dom and family face their greatest threat yet.",
                "VIN DIESEL, JASON MOMOA, JOHN CENA",
                "Action, Thriller",
                "English",
                "2h 15m"
            );

            MovieEntity avatar = new MovieEntity(
                "Avatar: Fire and Ash",
                "3D / IMAX",
                "Coming Soon",
                "December 18, 2026",
                "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600&auto=format&fit=crop&q=80",
                "Return to Pandora to discover the Ash People of fire. A new chapter in James Cameron's epic franchise.",
                "SAM WORTHINGTON, ZOE SALDANA, SIGOURNEY WEAVER",
                "Sci-Fi, Adventure",
                "English",
                "3h 10m"
            );

            movieRepo.saveAll(List.of(spiderman, odyssey, fastX, avatar));
            System.out.println("✅ Product: Seeded 4 movies");

            // Retrieve saved IDs
            Long spidermanId = spiderman.getId();
            Long odysseyId = odyssey.getId();

            // ===== SHOWTIMES FOR SPIDER-MAN =====
            String[][] spidermanSchedule = {
                {"Saturday", "8th, August 2026", "11:00 AM"},
                {"Saturday", "8th, August 2026", "02:00 PM"},
                {"Saturday", "8th, August 2026", "04:45 PM"},
                {"Saturday", "8th, August 2026", "07:45 PM"},
                {"Sunday", "9th, August 2026", "10:50 AM"},
                {"Sunday", "9th, August 2026", "01:50 PM"},
                {"Sunday", "9th, August 2026", "04:45 PM"},
                {"Sunday", "9th, August 2026", "07:45 PM"},
                {"Monday", "10th, August 2026", "10:50 AM"},
                {"Monday", "10th, August 2026", "01:50 PM"},
                {"Monday", "10th, August 2026", "04:45 PM"},
                {"Monday", "10th, August 2026", "07:45 PM"},
            };

            String[] theatres = {"Sony Square, Mirpur, Dhaka", "Bali Arcade, Chattogram"};
            String[] halls = {"Hall 1", "Hall 2"};

            for (String theatre : theatres) {
                for (String hall : halls) {
                    for (String[] schedule : spidermanSchedule) {
                        showtimeRepo.save(new ShowtimeEntity(
                            spidermanId, theatre, hall, schedule[1], schedule[2], schedule[0]
                        ));
                    }
                }
            }

            // ===== SHOWTIMES FOR THE ODYSSEY =====
            String[][] odysseySchedule = {
                {"Saturday", "8th, August 2026", "12:00 PM"},
                {"Saturday", "8th, August 2026", "03:30 PM"},
                {"Saturday", "8th, August 2026", "07:00 PM"},
                {"Sunday", "9th, August 2026", "12:00 PM"},
                {"Sunday", "9th, August 2026", "03:30 PM"},
                {"Sunday", "9th, August 2026", "07:00 PM"},
            };

            for (String theatre : theatres) {
                for (String[] schedule : odysseySchedule) {
                    showtimeRepo.save(new ShowtimeEntity(
                        odysseyId, theatre, "Hall 3", schedule[1], schedule[2], schedule[0]
                    ));
                }
            }

            System.out.println("✅ Product: Seeded showtimes for Spider-Man & The Odyssey");
        };
    }
}
