package com.team42.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<ShowtimeEntity, Long> {
    List<ShowtimeEntity> findByMovieId(Long movieId);
    List<ShowtimeEntity> findByMovieIdAndTheatre(Long movieId, String theatre);
}
