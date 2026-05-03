package com.example.demo.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

// The repository which is the interface for the user-related database operations
// It extends the JpaRepository in order to provide the built in CRUD functionality 

public interface UserRepository extends JpaRepository<UserAccount, Long> {
	
	// Finds a user by their username and this process happens during login
	Optional<UserAccount> findByUsername(String username);
	
	// This is what searches the user by username, their first name, or last name , it is also case insesntive
	List<UserAccount> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String username, String firstName, String lastName);
	
}
