package com.example.demo.seats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/seats")
public class SeatReservationController {

	private final SeatReservationService seatReservationService;

	public SeatReservationController(SeatReservationService seatReservationService) {
		this.seatReservationService = seatReservationService;
	}

	@GetMapping("/taken")
	public Map<String, Object> getTakenSeats(
		@RequestParam String movie,
		@RequestParam String showtime,
		@RequestParam(required = false) String date
	) {
		List<String> takenSeats = seatReservationService.getTakenSeats(movie.trim(), showtime.trim(), date);
		Map<String, Object> response = new HashMap<>();
		response.put("takenSeats", takenSeats);
		return response;
	}

	@PostMapping("/purchase")
	public ResponseEntity<Map<String, Object>> purchaseSeats(@RequestBody PurchaseRequest request) {
		SeatReservationService.PurchaseResult result = seatReservationService.reserveSeats(
			request.getMovie().trim(),
			request.getShowtime().trim(),
			request.getDate(),
			request.getSeats()
		);

		Map<String, Object> response = new HashMap<>();
		response.put("success", result.isSuccess());
		response.put("conflicts", result.getConflicts());

		if (result.isSuccess()) {
			return ResponseEntity.ok(response);
		}

		response.put("message", "Some seats are already purchased.");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
}
