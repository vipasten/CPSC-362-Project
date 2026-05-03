package com.example.demo.seats;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

// This is the entity that represents a seat reservation inside the system
// It maps to the seat_reservations table inside of the database
@Entity
@Table(
	name = "seat_reservations",

	// This is what ensures that the same seat can't be reserved two times
	// for the same movie, showtime, and the date
	uniqueConstraints = @UniqueConstraint(columnNames = {"movie_title", "showtime", "show_date", "seat_number"})
)
public class SeatReservation {

	// This is the primary key that is also auto generated
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// This is the title of the movie that is being reserved
	@Column(name = "movie_title", nullable = false)
	private String movieTitle;

	// This is the showtime for the reservation
	@Column(nullable = false)
	private String showtime;

	// The date for the showing
	@Column(name = "show_date", nullable = true)
	private String showDate;

	// This is the name of the seats or what identifies them such as A1 C5 etc
	@Column(name = "seat_number", nullable = false)
	private String seatNumber;

	// This is the username of the member who purchased the seat
	// Used to look up a member's ticket history on the member account page
	@Column(name = "username", nullable = true)
	private String username;

	// This is the order number generated on the frontend before purchase
	// Saved so the member can reference it when submitting a refund request
	@Column(name = "order_number", nullable = true)
	private String orderNumber;

	// This is the timestamp of whenever the reservation was made
	@Column(name = "purchased_at", nullable = false)
	private LocalDateTime purchasedAt;

	public Long getId() {
		return id;
	}

	// This is the getter for the movie title
	public String getMovieTitle() {
		return movieTitle;
	}

	// Then this is the setter for the movie title
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}

	// This is the getter for the showtime
	public String getShowtime() {
		return showtime;
	}

	// Then this is the setter for the showtime
	public void setShowtime(String showtime) {
		this.showtime = showtime;
	}

	// This is the getter for the show time date
	public String getShowDate() {
		return showDate;
	}

	// Then this is the setter for the showtime date
	public void setShowDate(String showDate) {
		this.showDate = showDate;
	}

	// This is the getter for the seat number
	public String getSeatNumber() {
		return seatNumber;
	}

	// This is the setter for the seat number
	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	// This is the getter for the username
	public String getUsername() {
		return username;
	}

	// This is the setter for the username
	public void setUsername(String username) {
		this.username = username;
	}

	// This is the getter for the order number
	public String getOrderNumber() {
		return orderNumber;
	}

	// This is the setter for the order number
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	// This is the getter for the purchase timestamp
	public LocalDateTime getPurchasedAt() {
		return purchasedAt;
	}

	// This is the setter for the purchase timestamp
	public void setPurchasedAt(LocalDateTime purchasedAt) {
		this.purchasedAt = purchasedAt;
	}
}