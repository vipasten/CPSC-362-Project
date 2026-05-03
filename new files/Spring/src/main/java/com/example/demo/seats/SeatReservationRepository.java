package com.example.demo.seats;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

// This is the repository for the seat reservation and database operations 
// It also extends JpaRepository in order to provide the built in CRUD functionality 
public interface SeatReservationRepository extends JpaRepository<SeatReservation, Long> {
	
	// This is what retreievs all of the seat reservations for the sepcific movie, showtime, and the date 
	// It is used to determine which of the seats are already taken 
	List<SeatReservation> findByMovieTitleAndShowtimeAndShowDate(String movieTitle, String showtime, String showDate);

	List<SeatReservation> findByUsername(String username);
}
