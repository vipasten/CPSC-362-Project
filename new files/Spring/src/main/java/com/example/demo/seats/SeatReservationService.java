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

	// This is the constructor injection for the repository
	public SeatReservationService(SeatReservationRepository seatReservationRepository) {
		this.seatReservationRepository = seatReservationRepository;
	}

	// This is a helper method to normalize the date value
	// Also it ensures consistency when querying the database
	private String normalizeDate(String showDate) {
		if (showDate == null || showDate.trim().isEmpty()) {
			return NO_DATE;
		}
		return showDate.trim();
	}

	// This is what retrieves the list of the taken seats for a given movie, showtime, and date
	// This is used by the frontend in order to disable the unavailable seats
	public List<String> getTakenSeats(String movieTitle, String showtime, String showDate) {
		String normalizedDate = normalizeDate(showDate);

		List<SeatReservation> rows = seatReservationRepository.findByMovieTitleAndShowtimeAndShowDate(
			movieTitle,
			showtime,
			normalizedDate
		);

		return rows.stream().map(SeatReservation::getSeatNumber).collect(Collectors.toList());
	}

	// This retrieves all seat reservations made by a specific username
	// Used by the member account page to show the user their ticket history
	public List<SeatReservation> getTicketsByUsername(String username) {
		return seatReservationRepository.findByUsername(username);
	}

	// This handles the seat reservation logic including conflict detection and saving
	// Accepts username and orderNumber to save with each reservation
	@Transactional
	public PurchaseResult reserveSeats(String movieTitle, String showtime, String showDate, List<String> requestedSeats, String username, String orderNumber) {

		String normalizedDate = normalizeDate(showDate);

		// Clean up requested seat list - remove extra spaces and empty values
		List<String> normalizedSeats = requestedSeats.stream()
			.map(String::trim)
			.filter(seat -> !seat.isEmpty())
			.collect(Collectors.toList());

		// Get current taken seats from database
		List<String> taken = getTakenSeats(movieTitle, showtime, normalizedDate);
		Set<String> takenSet = new HashSet<>(taken);

		// Check for conflicts
		List<String> conflicts = normalizedSeats.stream()
			.filter(takenSet::contains)
			.collect(Collectors.toList());

		if (!conflicts.isEmpty()) {
			return new PurchaseResult(false, conflicts);
		}

		// Build reservation objects for each selected seat
		List<SeatReservation> newRows = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		for (String seat : normalizedSeats) {
			SeatReservation reservation = new SeatReservation();
			reservation.setMovieTitle(movieTitle);
			reservation.setShowtime(showtime);
			reservation.setShowDate(normalizedDate);
			reservation.setSeatNumber(seat);
			reservation.setPurchasedAt(now);

			// Save username so we can look up tickets by user later
			reservation.setUsername(username);

			// Save order number so member can reference it for refunds
			reservation.setOrderNumber(orderNumber);

			newRows.add(reservation);
		}

		try {
			seatReservationRepository.saveAll(newRows);
			return new PurchaseResult(true, new ArrayList<>());
		} catch (DataIntegrityViolationException ex) {
			List<String> latestTaken = getTakenSeats(movieTitle, showtime, normalizedDate);
			Set<String> latestTakenSet = new HashSet<>(latestTaken);
			List<String> latestConflicts = normalizedSeats.stream()
				.filter(latestTakenSet::contains)
				.collect(Collectors.toList());
			return new PurchaseResult(false, latestConflicts);
		}
	}

	// Helper class to return the result of the purchase attempt
	public static class PurchaseResult {
		private final boolean success;
		private final List<String> conflicts;

		public PurchaseResult(boolean success, List<String> conflicts) {
			this.success = success;
			this.conflicts = conflicts;
		}

		public boolean isSuccess() {
			return success;
		}

		public List<String> getConflicts() {
			return conflicts;
		}
	}
}