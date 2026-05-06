package com.example.demo.auth.Support;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    // Finds a user by their username and this process happens post the submission of a support ticket
	Optional<SupportTicket> findByUsername(String username);
	
	// This is what searches the use	r by username, their first name, or last name , it is also case insesntive
	List<SupportTicket> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String username, String firstName, String lastName);
}
