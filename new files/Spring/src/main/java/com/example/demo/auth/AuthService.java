package com.example.demo.auth;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

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
}
