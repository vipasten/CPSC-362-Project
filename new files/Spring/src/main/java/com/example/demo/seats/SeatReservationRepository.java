package com.example.demo.seats;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
	List<SeatReservation> findByMovieTitleAndShowtimeAndShowDate(String movieTitle, String showtime, String showDate);
}
