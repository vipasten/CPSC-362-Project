package com.example.demo.auth;

import java.util.Optional;
import java.util.List;
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
		user.setSignupDate(LocalDateTime.now());
		userRepository.save(user);
		return true;
	}

	public boolean login(String username, String rawPassword) {
		Optional<UserAccount> possibleUser = userRepository.findByUsername(username);
		if (!possibleUser.isPresent()) {
			return false;
		}

		UserAccount user = possibleUser.get();
		return passwordEncoder.matches(rawPassword, user.getPasswordHash());
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
