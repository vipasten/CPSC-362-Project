package com.example.demo.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.List;

// The controller with the big responsibiliity for handling authentication and the basic naviagtion 
// This handles signup and login alo viewing membership data 
@Controller
public class AuthController {

	// References then to the service layer and that is where the authentication logic is then handled 
	private final AuthService authService;

	// The constructo injection which allows the Spring to provide AuthService automatically 
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
	// This also takes the users input and then sends to to the service layer for the creation of an account 
	@PostMapping("/signup")
	public String signup(
		@RequestParam String fname,
		@RequestParam String lname,
		@RequestParam String username,
		@RequestParam String password
	) {

		// This is a trim input that removes any extra spaces 
		String firstName = fname.trim();
		String lastName = lname.trim();
		String userName = username.trim();

		// boolean - calls the service to attempt the creation of an account 
		boolean created = authService.signUp(firstName, lastName, userName, password);
		
		// If the attempt is successful, then the user is redirected to the home page with a flag of success 
		if (created) {
			return "redirect:/html/index.html?signup=success";
		}

		// However if the username is already taken, the user will be redirected back to the sign up page 
		// with an error 
		return "redirect:/html/pages/signup.html?error=username-taken";
	}

	// Endpoint for Login
	
	// This is what handles the login form submission from the user 
	// and then it will validate the credentials and then follow up by determing the users role 
	@PostMapping("/login")
	public String login(
		@RequestParam String username,
		@RequestParam String password
	) {

		// Trinm the username just to avoid any sort of mismatches 
		String userName = username.trim();
		
		// this is a call to service in order to validate the login and then return the role 
		String role = authService.loginAndGetRole(userName, password);

		// This will redirect the user based on the role in other words (role-base routing)
		if ("ADMIN".equals(role)) {
			return "redirect:/html/pages/adminhomepage.html?login=success";
		}
		if ("EMPLOYEE".equals(role)) {
			return "redirect:/html/pages/employeehomepage.html?login=success";
		}
		if ("MEMBER".equals(role)) {
			return "redirect:/html/index.html?login=success";
		}

		// In any event the login fails user is redirected to the login page with an error 
		return "redirect:/html/pages/login.html?error=invalid-credentials";
	}

	// Membership Viewing (Admin feauture )
	
	// This displays the list of users that can be filtered by a search query 
	// used for all of the viewing membership data 
	@GetMapping("/viewmembership")
	public String viewUsers(@RequestParam(required = false) String search, Model model) {
		
		// This is what retrieves the filtered or full user list from the service 
		List<UserAccount> users = authService.searchUsers(search);
		
		// Add user list to the model for frontend rendering 
		model.addAttribute("users", users);
		
		// Perserves the search input that way it stays in the UI 
		model.addAttribute("searchQuery", search != null ? search : "");
		
		// This return the view name HTML page 
		return "viewmembership";
	}
}
