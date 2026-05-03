package com.example.demo.seats;

import java.util.List;

// This is used to carry the purchase request data between the frontend to the backend
// It contains the movie info and also includes info with the selected seats
public class PurchaseRequest {

	// This is the movie that is selected by the user
	private String movie;

	// This is the showtime that is selected by the user
	private String showtime;

	// This is the date of the movie showing
	private String date;

	// This is the list of the seats that have been selected
	private List<String> seats;

	// This is the order number generated on the frontend before purchase
	// Saved with each seat reservation so it can be displayed on the member account page
	private String orderNumber;

	// This is the getter for the movie
	public String getMovie() {
		return movie;
	}

	// This is the setter for the movie
	public void setMovie(String movie) {
		this.movie = movie;
	}

	// This is the getter for the showtime
	public String getShowtime() {
		return showtime;
	}

	// This is the setter for the showtime
	public void setShowtime(String showtime) {
		this.showtime = showtime;
	}

	// This is the getter for the date
	public String getDate() {
		return date;
	}

	// This is the setter for the date
	public void setDate(String date) {
		this.date = date;
	}

	// The getter for the seats that have been selected
	public List<String> getSeats() {
		return seats;
	}

	// The setter for the seats that have been selected
	public void setSeats(List<String> seats) {
		this.seats = seats;
	}

	// This is the getter for the order number
	public String getOrderNumber() {
		return orderNumber;
	}

	// This is the setter for the order number
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
}