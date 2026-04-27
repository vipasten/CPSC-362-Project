package com.example.demo.auth;

import java.util.Optional;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public AuthService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@PostConstruct
	public void initializeDefaultAccounts() {
		createDefaultUser("Admin", "User", "admin", "admin123", "ADMIN");
		createDefaultUser("Employee", "User", "employee", "employee123", "EMPLOYEE");
	}

	private void createDefaultUser(String firstName, String lastName, String username, String rawPassword, String role) {
		Optional<UserAccount> existingUser = userRepository.findByUsername(username);
		if (existingUser.isPresent()) {
			UserAccount user = existingUser.get();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setRole(role);
			user.setPasswordHash(passwordEncoder.encode(rawPassword));
			userRepository.save(user);
			return;
		}

		UserAccount user = new UserAccount();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(rawPassword));
		user.setRole(role);
		user.setSignupDate(LocalDateTime.now());
		userRepository.save(user);
	}

	public boolean signUp(String firstName, String lastName, String username, String rawPassword) {
		Optional<UserAccount> foundUser = userRepository.findByUsername(username);
		if (foundUser.isPresent()) {
			return false;
		}

		UserAccount user = new UserAccount();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setUsername(username);
		user.setPasswordHash(passwordEncoder.encode(rawPassword));
		user.setRole("MEMBER");
		user.setSignupDate(LocalDateTime.now());
		userRepository.save(user);
		return true;
	}

	public String loginAndGetRole(String username, String rawPassword) {
		Optional<UserAccount> possibleUser = userRepository.findByUsername(username);
		if (!possibleUser.isPresent()) {
			return null;
		}

		UserAccount user = possibleUser.get();
		if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
			return null;
		}

		String role = user.getRole();
		if (role == null || role.trim().isEmpty()) {
			return "MEMBER";
		}
		return role.toUpperCase();
	}

	public List<UserAccount> getAllUsers() {
		return userRepository.findAll();
	}

	public List<UserAccount> searchUsers(String query) {
		if (query == null || query.trim().isEmpty()) {
			return getAllUsers();
		}
		return userRepository.findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, query);
	}
}
