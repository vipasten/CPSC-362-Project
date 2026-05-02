package com.example.demo.seats;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// The service layer and this is what is actually responsible for all the seat reservation logic 
// This is where the conflict detection and saving reservation actually happens 
@Service
public class SeatReservationService {

	private static final String NO_DATE = "NO_DATE";
	private final SeatReservationRepository seatReservationRepository;

	// This is the constructor Injection for the repository 
	// This is what will allow access to the databse operation, that are related to seat reservation
	
	public SeatReservationService(SeatReservationRepository seatReservationRepository) {
		this.seatReservationRepository = seatReservationRepository;
	}

	// This is a helper method that is to normalize the date value 
	// also it ensure that consistency is ther when querying the databse 
	private String normalizeDate(String showDate) {
		if (showDate == null || showDate.trim().isEmpty()) {
			return NO_DATE;
		}
		return showDate.trim();
	}

	// This is what is going to retrieve the list of the taken seats for a given movie, showtim, and date
	// This is used by the frontend in order to disable the unavailable seats 
	public List<String> getTakenSeats(String movieTitle, String showtime, String showDate) {
		String normalizedDate = normalizeDate(showDate);
		
		// This is going to fetch all of the reservation rows in the database
		List<SeatReservation> rows = seatReservationRepository.findByMovieTitleAndShowtimeAndShowDate(
			movieTitle,
			showtime,
			normalizedDate
		);
		
		// This extracts only seat numbers from objects in reservation 
		return rows.stream().map(SeatReservation::getSeatNumber).collect(Collectors.toList());
	}

	// This is the meat and potatos that handles the seat reservation logic
	// What it includes also is the conflict detection and saving of seats 
	@Transactional
	public PurchaseResult reserveSeats(String movieTitle, String showtime, String showDate, List<String> requestedSeats) {
		
		// This normalize date so the format can remain consistent 
		String normalizedDate = normalizeDate(showDate);
		
		// This is what clean up requested seat list essentially removes the empty spaces and the empty values 
		List<String> normalizedSeats = requestedSeats.stream()
			.map(String::trim)
			.filter(seat -> !seat.isEmpty())
			.collect(Collectors.toList());

		// This will get the current state of the taken seats from the database 
		List<String> taken = getTakenSeats(movieTitle, showtime, normalizedDate);
		
		// This then converts to set in order for faster lookup 
		Set<String> takenSet = new HashSet<>(taken);
		
		// The checker for any conflicts like seats that are already taken 
		List<String> conflicts = normalizedSeats.stream()
			.filter(takenSet::contains)
			.collect(Collectors.toList());

		// If there happens to be conflict it will return a false result 
		if (!conflicts.isEmpty()) {
			return new PurchaseResult(false, conflicts);
		}

		// This then prepare new reservation for saving 
		List<SeatReservation> newRows = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		// This build reservation objects for each of the selected seats 
		for (String seat : normalizedSeats) {
			SeatReservation reservation = new SeatReservation();
			reservation.setMovieTitle(movieTitle);
			reservation.setShowtime(showtime);
			reservation.setShowDate(normalizedDate);
			reservation.setSeatNumber(seat);
			// Timestamp feauture to record 
			reservation.setPurchasedAt(now);
			newRows.add(reservation);
		}

		try {
			// Attempt to save all of the reservations at once 
			seatReservationRepository.saveAll(newRows);
			
			// This will return success if only if everything is saved 
			return new PurchaseResult(true, new ArrayList<>());
		} catch (DataIntegrityViolationException ex) {
			
			// If a database conflict does arise or in other word race condition
			// the re-checking taken seats in order to find out whichs ones were the ones causing the issue 
			List<String> latestTaken = getTakenSeats(movieTitle, showtime, normalizedDate);
			Set<String> latestTakenSet = new HashSet<>(latestTaken);
			List<String> latestConflicts = normalizedSeats.stream()
				.filter(latestTakenSet::contains)
				.collect(Collectors.toList());
			
			// This is what will return failure with an updated conflict list 
			return new PurchaseResult(false, latestConflicts);
		}
	}

	// This is a helper class that is used to return the result of the purchase attempt 
	// Which also includes success status and any conflicting seats
	public static class PurchaseResult {
		private final boolean success;
		private final List<String> conflicts;

		public PurchaseResult(boolean success, List<String> conflicts) {
			this.success = success;
			this.conflicts = conflicts;
		}

		// This indicates whether the reservation was indeed successful 
		public boolean isSuccess() {
			return success;
		}

		// This will return the lists of seats that did cause the conflicts 
		public List<String> getConflicts() {
			return conflicts;
		}
	}
}
