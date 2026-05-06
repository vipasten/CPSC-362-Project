package com.example.demo.auth;

import java.util.Optional;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

// This is the service layer and is ultimatly is resposnible for handling aunthetication logic 
// This includes things such as signup, login validation, and user managment 
@Service
public class AuthService {

	private final UserRepository userRepository;
	
	// The Bcrypt password encoder in order to securly hash password in order to store them
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	// This is the constructor injection for the repository 
	public AuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// This will run automatically when the application starts up
	// This is used in otder to create the default admin and employee accounts 
	@PostConstruct
	public void initializeDefaultAccounts() {
		createDefaultUser("Admin", "User", "admin", "admin123", "ADMIN");
		createDefaultUser("Employee", "User", "employee", "employee123", "EMPLOYEE");
	}

	// This is the helper method that is going to create or update the default accounts 
	// Also it ensures that these accoutn will forever exist in the system 
	private void createDefaultUser(String firstName, String lastName, String username, String rawPassword, String role) {
		
		// This is the check to see if the user already exist in the system
		Optional<UserAccount> existingUser = userRepository.findByUsername(username);
		if (existingUser.isPresent()) {
			
			// If the user does exist, then it will update their information 
			UserAccount user = existingUser.get();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setRole(role);
			
			// Additonal security to re-hash password in oreder to keep it secure
			user.setPasswordHash(passwordEncoder.encode(rawPassword));
			userRepository.save(user);
			return;
		}

		// If the user does not exist this is the path to creating a new one 
		UserAccount user = new UserAccount();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		
		// This stores the password as a hashed password instead of it being plaintext 
		user.setPasswordHash(passwordEncoder.encode(rawPassword));
		user.setRole(role);
		
		// This will record the time of when the user signup
		user.setSignupDate(LocalDateTime.now());
		userRepository.save(user);
	}

	// This is what handles all the user signup logic 
	// Returns false if the username already exist inside the system
	public boolean signUp(String firstName, String lastName, String username, String rawPassword) {
		
		// Checks if the username has already been taken 
		Optional<UserAccount> foundUser = userRepository.findByUsername(username);
		if (foundUser.isPresent()) {
			return false;
		}

		// Creates the new user account
		UserAccount user = new UserAccount();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		
		// Hash password before saving just a good security measure 
		user.setPasswordHash(passwordEncoder.encode(rawPassword));
		
		// This assings the default value of their role memeber 
		user.setRole("MEMBER");
		
		// This saves the signup Timestamp
		user.setSignupDate(LocalDateTime.now());
		userRepository.save(user);
		return true;
	}

	
	// This is what will be handling the login logic 
	// And it will return the users role if the login is successful 
	public String loginAndGetRole(String username, String rawPassword) {
		
		// This is what will attempt to find the user by their username 
		Optional<UserAccount> possibleUser = userRepository.findByUsername(username);
		
		// If the user does not exist, then it will return null and the login will fail
		if (!possibleUser.isPresent()) {
			return null;
		}

		UserAccount user = possibleUser.get();
		
		// The password checker to make sure the password mathces the stored hash 
		if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
			return null;
		}

		// This is what will retieve the role and ensure that it is valid 
		String role = user.getRole();
		if (role == null || role.trim().isEmpty()) {
			return "MEMBER";
		}
		
		// Then it will return the roll in uppercase for the consistency 
		return role.toUpperCase();
	}

	// This is what will retrieve all of the users from the database 
	public List<UserAccount> getAllUsers() {
		return userRepository.findAll();
	}

	// This is what searches users based on the username, first name or their last name
	public List<UserAccount> searchUsers(String query) {
		
		// If no search query is provided, then it will return all users
		if (query == null || query.trim().isEmpty()) {
			return getAllUsers();
		}
		
		// This is what performs the case-senstive search across multiple fields for cosnsitency 
		return userRepository.findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, query);
	}
}
