package com.example.demo.seats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// This is the controller that is responsible for handling all of the seat-related API requests
// This would include retrieving taken seats and processing seat purchases
@RestController
@RequestMapping("/api/seats")
public class SeatReservationController {

	// Reference to the service layer and that is where the actual business logic happens
	private final SeatReservationService seatReservationService;

	// Constructor injection - allows Spring to automatically provide the service dependency
	public SeatReservationController(SeatReservationService seatReservationService) {
		this.seatReservationService = seatReservationService;
	}

	// GET /api/seats/taken
	// Returns the list of seat IDs that have already been reserved for a given movie, showtime, and date
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

	// POST /api/seats/purchase
	// Triggered when the user purchases seats
	// Gets username from HTTP session and orderNumber from the request body
	@PostMapping("/purchase")
	public ResponseEntity<Map<String, Object>> purchaseSeats(
		@RequestBody PurchaseRequest request,
		HttpSession session
	) {
		// Get the username from the HTTP session set during login
		// If not logged in this will be null and the purchase still goes through as a guest
		String username = (String) session.getAttribute("username");

		// Get the order number sent from paymentpage.html
		String orderNumber = request.getOrderNumber();

		// Call the service with username and orderNumber so both get saved with the reservation
		SeatReservationService.PurchaseResult result = seatReservationService.reserveSeats(
			request.getMovie().trim(),
			request.getShowtime().trim(),
			request.getDate(),
			request.getSeats(),
			username,
			orderNumber
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

	// GET /api/seats/mine
	// Returns all seat reservations for the currently logged in user
	// Used by memberaccount.html to display the user's ticket history
	@GetMapping("/mine")
	public ResponseEntity<Map<String, Object>> getMyTickets(HttpSession session) {
		Map<String, Object> response = new HashMap<>();

		// Get the username from the session
		String username = (String) session.getAttribute("username");

		// If not logged in return an unauthorized error
		if (username == null) {
			response.put("success", false);
			response.put("message", "Not logged in.");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		// Fetch all reservations for this user from the database
		List<SeatReservation> reservations = seatReservationService.getTicketsByUsername(username);

		// Build a clean list of ticket info to send to the frontend
		List<Map<String, Object>> tickets = new ArrayList<>();
		for (SeatReservation r : reservations) {
			Map<String, Object> ticket = new HashMap<>();
			ticket.put("movie", r.getMovieTitle());
			ticket.put("showtime", r.getShowtime());
			ticket.put("date", r.getShowDate());
			ticket.put("seat", r.getSeatNumber());
			ticket.put("orderNumber", r.getOrderNumber());
			ticket.put("purchasedAt", r.getPurchasedAt());
			tickets.add(ticket);
		}

		response.put("success", true);
		response.put("username", username);
		response.put("tickets", tickets);
		return ResponseEntity.ok(response);
	}
}