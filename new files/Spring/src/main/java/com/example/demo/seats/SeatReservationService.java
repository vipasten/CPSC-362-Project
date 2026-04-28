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

@Service
public class SeatReservationService {

	private static final String NO_DATE = "NO_DATE";
	private final SeatReservationRepository seatReservationRepository;

	public SeatReservationService(SeatReservationRepository seatReservationRepository) {
		this.seatReservationRepository = seatReservationRepository;
	}

	private String normalizeDate(String showDate) {
		if (showDate == null || showDate.trim().isEmpty()) {
			return NO_DATE;
		}
		return showDate.trim();
	}

	public List<String> getTakenSeats(String movieTitle, String showtime, String showDate) {
		String normalizedDate = normalizeDate(showDate);
		List<SeatReservation> rows = seatReservationRepository.findByMovieTitleAndShowtimeAndShowDate(
			movieTitle,
			showtime,
			normalizedDate
		);
		return rows.stream().map(SeatReservation::getSeatNumber).collect(Collectors.toList());
	}

	@Transactional
	public PurchaseResult reserveSeats(String movieTitle, String showtime, String showDate, List<String> requestedSeats) {
		String normalizedDate = normalizeDate(showDate);
		List<String> normalizedSeats = requestedSeats.stream()
			.map(String::trim)
			.filter(seat -> !seat.isEmpty())
			.collect(Collectors.toList());

		List<String> taken = getTakenSeats(movieTitle, showtime, normalizedDate);
		Set<String> takenSet = new HashSet<>(taken);
		List<String> conflicts = normalizedSeats.stream()
			.filter(takenSet::contains)
			.collect(Collectors.toList());

		if (!conflicts.isEmpty()) {
			return new PurchaseResult(false, conflicts);
		}

		List<SeatReservation> newRows = new ArrayList<>();
		LocalDateTime now = LocalDateTime.now();

		for (String seat : normalizedSeats) {
			SeatReservation reservation = new SeatReservation();
			reservation.setMovieTitle(movieTitle);
			reservation.setShowtime(showtime);
			reservation.setShowDate(normalizedDate);
			reservation.setSeatNumber(seat);
			reservation.setPurchasedAt(now);
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
