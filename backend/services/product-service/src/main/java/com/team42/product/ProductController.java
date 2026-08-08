package com.team42.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ProductController {

    private final MovieRepository movieRepository;
    private final ShowtimeRepository showtimeRepository;

    public ProductController(MovieRepository movieRepository, ShowtimeRepository showtimeRepository) {
        this.movieRepository = movieRepository;
        this.showtimeRepository = showtimeRepository;
    }

    // ===== Movie Endpoints =====

    @GetMapping("/api/v1/movies")
    public List<MovieEntity> getAllMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/api/v1/movies/{id}")
    public ResponseEntity<MovieEntity> getMovieById(@PathVariable("id") Long id) {
        return movieRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===== Showtime Endpoints =====

    @GetMapping("/api/v1/movies/{movieId}/showtimes")
    public List<ShowtimeEntity> getShowtimes(@PathVariable("movieId") Long movieId,
                                             @RequestParam(value = "theatre", required = false) String theatre) {
        if (theatre != null && !theatre.isBlank()) {
            return showtimeRepository.findByMovieIdAndTheatre(movieId, theatre);
        }
        return showtimeRepository.findByMovieId(movieId);
    }

    @GetMapping("/api/v1/showtimes/{id}")
    public ResponseEntity<ShowtimeEntity> getShowtimeById(@PathVariable("id") Long id) {
        return showtimeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/v1/showtimes")
    public List<ShowtimeEntity> getAllShowtimes() {
        return showtimeRepository.findAll();
    }

    // ===== Health Endpoint =====
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "product-service"));
    }
}
