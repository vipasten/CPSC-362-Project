package com.example.demo.auth;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
		String role = authService.loginAndGetRole(userName, password);

		if ("ADMIN".equals(role)) {
			return "redirect:/html/pages/adminhomepage.html?login=success";
		}
		if ("EMPLOYEE".equals(role)) {
			return "redirect:/html/pages/employeehomepage.html?login=success";
		}
		if ("MEMBER".equals(role)) {
			return "redirect:/html/index.html?login=success";
		}

		return "redirect:/html/pages/login.html?error=invalid-credentials";
	}

	@GetMapping("/viewmembership")
	public String viewUsers(@RequestParam(required = false) String search, Model model) {
		List<UserAccount> users = authService.searchUsers(search);
		model.addAttribute("users", users);
		model.addAttribute("searchQuery", search != null ? search : "");
		return "viewmembership";
	}}