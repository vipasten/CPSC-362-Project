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

// This is the controller that is responsible for handling all of the seat-related API request
// This would include retrieving taken seats and processing seat purchases
@RestController
@RequestMapping("/api/seats")
public class SeatReservationController {

	// Refernce to the service layer and that is where the acutal business logic happens
	// This controller should only handle the request and the responses only
	private final SeatReservationService seatReservationService;

	// Injection of constructed the more efficent way in Spring
	// which also allows the spring to automatically provide the service depenedency
	public SeatReservationController(SeatReservationService seatReservationService) {
		this.seatReservationService = seatReservationService;
	}

	// GET TAKEN SEATS ENDPOINT!!!

	// This is the endpoint that will be used by the frontend in order to load unavalible seats 
	// It will also return the list of seat IDs that already been reserved 
	@GetMapping("/taken")
	public Map<String, Object> getTakenSeats(
		@RequestParam String movie,
		@RequestParam String showtime,
		@RequestParam(required = false) String date
	) {
		// This will trim the inputs for the case there are extra spaces from the frontend 
		// Also key to note this helps avoid mismatches when querying the database
		List<String> takenSeats = seatReservationService.getTakenSeats(movie.trim(), showtime.trim(), date);
		
		// This creates the response object in order to send data back to frontend
		// and in using the map it allows flexibility for addiotnal fields if that is needed later 
		Map<String, Object> response = new HashMap<>();
		
		// This adds taken seats to the response 
		// The frontend uses this essentially to disable reserved seat in the UI 
		response.put("takenSeats", takenSeats);
		// Returns the response and as JSON
		return response;
	}

	// Endpont that is triggere when the user attempts to purchase one or more seats 
	// This will send the selected seats and it will show the info to the backend for the validation along with saving 
	@PostMapping("/purchase")
	public ResponseEntity<Map<String, Object>> purchaseSeats(@RequestBody PurchaseRequest request) {
		
		// This is the a call to the service layer in order to handle reservation logic 
		// this includes and is sure to check for conflicts and savinf the valid seats 
		SeatReservationService.PurchaseResult result = seatReservationService.reserveSeats(
			request.getMovie().trim(),
			request.getShowtime().trim(),
			request.getDate(),
			request.getSeats()
		);

		// Preporation response object to return the purchase result 
		Map<String, Object> response = new HashMap<>();
		
		// This indicates essentially if the reservation was indeed successful 
		response.put("success", result.isSuccess());
		
		// This is for the list of seats that could not be reserved due to conflict (already taken)
		response.put("conflicts", result.getConflicts());

		// if the resevation does succeed then it will return 200 OK 
		if (result.isSuccess()) {
			return ResponseEntity.ok(response);
		}

		// If there is a conflict that does exist then it will notify the user and return 409 Conflict 
		// This also prevents double booking or duplicate seat purchasing 
		response.put("message", "Some seats are already purchased.");
		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}
}
