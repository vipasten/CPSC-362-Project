package com.example.demo.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

// This is the entity that represents a user account inside the system
// This maps directly to the user table that is inside the database
@Entity
@Table(name = "users")
public class UserAccount {

	// Here is the primary key for the user that is also auto generated 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// The users first name which is a required field 
	@Column(nullable = false)
	private String firstName;

	// This is the users last name which is also a required field 
	@Column(nullable = false)
	private String lastName;

	// This is the unique username that is used when a user logs in
	@Column(nullable = false, unique = true)
	private String username;

	
	// This is what stores the hashed password, jsut to reiterate never store plaintext for security reasons
	@Column(nullable = false)
	private String passwordHash;

	// This is the role of the users being, ADMIN , EMPLOYEE, MEMEBR 
	@Column(nullable = true)
	private String role;

	// These are the Timestamps of when the user signup 
	@Column(nullable = true)
	private LocalDateTime signupDate;

	// The default constructor that is required by JPA 
	public UserAccount() {}

	public Long getId() {
		return id;
	}

	// The getter for the first name 
	public String getFirstName() {
		return firstName;
	}

	// This is the getter for the last name 
	public String getLastName() {
		return lastName;
	}

	
	public String getUsername() {
		return username;
	}

	// This is the getter for the password hash
	public String getPasswordHash() {
		return passwordHash;
	}

	// This is the getter for the user role
	public String getRole() {
		return role;
	}

	// This is the setter for the first name 
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	// This is the setter for the last name 
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// This is the setter for the username  
	public void setUsername(String username) {
		this.username = username;
	}

	// This is the setter for the hash  
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	// This is the setter for the role 
	public void setRole(String role) {
		this.role = role;
	}

	// This is the getter for the signup date 
	public LocalDateTime getSignupDate() {
		return signupDate;
	}

	// This is the setter for sign up date 
	public void setSignupDate(LocalDateTime signupDate) {
		this.signupDate = signupDate;
	}
}
