package com.example.demo.seats;

import java.util.List;

// This is used to carry the purchase request data between the frontend to the backend 
// It contains the movie info and also include info with the selected seats 
public class PurchaseRequest {

	// This is the moviie that is selected by the user 
	private String movie;
	
	// This is the showtime that is selcted by the user 
	private String showtime;
	
	// This is the date of the movie showing 
	private String date;
	
	// This is the list of the seats that have been selected 
	private List<String> seats;

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
}
