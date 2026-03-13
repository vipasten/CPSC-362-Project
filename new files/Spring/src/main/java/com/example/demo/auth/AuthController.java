package com.example.demo.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/")
	public String home() {
		return "redirect:/html/index.html";
	}

	@PostMapping("/signup")
	public String signup(
		@RequestParam String fname,
		@RequestParam String lname,
		@RequestParam String username,
		@RequestParam String password
	) {
		String firstName = fname.trim();
		String lastName = lname.trim();
		String userName = username.trim();

		boolean created = authService.signUp(firstName, lastName, userName, password);
		if (created) {
			return "redirect:/html/index.html?signup=success";
		}

		return "redirect:/html/pages/signup.html?error=username-taken";
	}

	@PostMapping("/login")
	public String login(
		@RequestParam String username,
		@RequestParam String password
	) {
		String userName = username.trim();
		boolean loggedIn = authService.login(userName, password);

		if (loggedIn) {
			return "redirect:/html/index.html?login=success";
		}

		return "redirect:/html/pages/login.html?error=invalid-credentials";
	}
}
