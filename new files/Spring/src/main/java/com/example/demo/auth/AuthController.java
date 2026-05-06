package com.example.demo.auth;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;

// The controller with the big responsibility for handling authentication and the basic navigation
// This handles signup and login also viewing membership data
@Controller
public class AuthController {

	// References to the service layer and that is where the authentication logic is then handled
	private final AuthService authService;

	// The constructor injection which allows Spring to provide AuthService automatically
	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// Default route to the homepage
	// Essentially just redirects the user to the main HTML index page
	@GetMapping("/")
	public String home() {
		return "redirect:/html/index.html";
	}

	// Endpoint for Signup
	// This is what handles the signup form submission requested from the frontend
	// This also takes the users input and then sends it to the service layer for the creation of an account
	@PostMapping("/signup")
	public String signup(
		@RequestParam String fname,
		@RequestParam String lname,
		@RequestParam String username,
		@RequestParam String password,
		HttpSession session
	) {
		// This is a trim input that removes any extra spaces
		String firstName = fname.trim();
		String lastName = lname.trim();
		String userName = username.trim();

		// boolean - calls the service to attempt the creation of an account
		boolean created = authService.signUp(firstName, lastName, userName, password);

		// If the attempt is successful, save the username and role to the session
		// then redirect the user to the home page with a flag of success
		if (created) {
			session.setAttribute("username", userName);
			session.setAttribute("role", "MEMBER");
			return "redirect:/html/index.html?signup=success";
		}

		// However if the username is already taken, the user will be redirected back to the sign up page
		// with an error
		return "redirect:/html/pages/signup.html?error=username-taken";
	}

	// Endpoint for Login
	// This is what handles the login form submission from the user
	// and then it will validate the credentials and then follow up by determining the users role
	@PostMapping("/login")
	public String login(
		@RequestParam String username,
		@RequestParam String password,
		HttpSession session
	) {
		// Trim the username just to avoid any sort of mismatches
		String userName = username.trim();

		// This is a call to service in order to validate the login and then return the role
		String role = authService.loginAndGetRole(userName, password);

		// This will redirect the user based on the role (role-based routing)
		// Also saves the username and role to the HTTP session for use by other endpoints
		if ("ADMIN".equals(role)) {
			session.setAttribute("username", userName);
			session.setAttribute("role", "ADMIN");
			return "redirect:/html/pages/adminhomepage.html?login=success";
		}
		if ("EMPLOYEE".equals(role)) {
			session.setAttribute("username", userName);
			session.setAttribute("role", "EMPLOYEE");
			return "redirect:/html/pages/employeehomepage.html?login=success";
		}
		if ("MEMBER".equals(role)) {
			session.setAttribute("username", userName);
			session.setAttribute("role", "MEMBER");
			return "redirect:/html/index.html?login=success";
		}

		// In any event the login fails user is redirected to the login page with an error
		return "redirect:/html/pages/login.html?error=invalid-credentials";
	}

	// GET /api/session
	// Returns the currently logged in user's username and role from the HTTP session
	// Used by the frontend to personalize pages and know who is logged in
	@GetMapping("/api/session")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getSession(HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		String username = (String) session.getAttribute("username");
		String role = (String) session.getAttribute("role");

		if (username != null) {
			response.put("loggedIn", true);
			response.put("username", username);
			response.put("role", role);
		} else {
			response.put("loggedIn", false);
		}
		return ResponseEntity.ok(response);
	}

	// GET /logout
	// Clears the HTTP session and redirects to the login page
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/html/pages/login.html";
	}

	// Membership Viewing (Admin feature)
	// This displays the list of users that can be filtered by a search query
	// used for all of the viewing membership data
	@GetMapping("/viewmembership")
	public String viewUsers(@RequestParam(required = false) String search, Model model) {

		// This is what retrieves the filtered or full user list from the service
		List<UserAccount> users = authService.searchUsers(search);

		// Add user list to the model for frontend rendering
		model.addAttribute("users", users);

		// Preserves the search input that way it stays in the UI
		model.addAttribute("searchQuery", search != null ? search : "");

		// This returns the view name HTML page
		return "viewmembership";
	}
}