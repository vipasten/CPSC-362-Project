package com.example.demo.auth.Support;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "support_tickets")
public class SupportTicket {
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

	// This is the username is also a required field
	@Column(nullable = false)
	private String username;

    // This is the subject of the support ticket
	@Column(nullable = false)
    private String subject;

    // This is the description of the support ticket
    @Column(nullable = false, columnDefinition="TEXT")
    private String description;

	// These are the Timestamps of ticket creation
	@Column(nullable = true)
	private LocalDateTime postedDate;

	// Bool if ticket is resolved or not
	@Column(nullable = false)
	private boolean resolved = false;

	// The default constructor that is required by JPA
	public SupportTicket() {}

	//THESE ARE ALL THE GETTERS AND SETTERS FOR SUPPORT TICKET
	public Long getId() {
		return id;
	}

	// Firstname
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	// Last name
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	// Subject
	public String getSubject() {
		return subject;
	}
    public void setSubject(String subject) {
        this.subject = subject;
    }


	// Description
	public String getDescription() {
		return description;
	}
    public void setDescription(String description) {
        this.description = description;
    }

	// Username
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}


    // ticket creation date
	public LocalDateTime getPostedDate() {
		return postedDate;
	}
	public void setPostedDate(LocalDateTime postedDate) {
		this.postedDate = postedDate;
	}

	// Resolved Status
	public boolean isResolved() {
		return resolved;
	}
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
}
