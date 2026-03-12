package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

@Controller // Marks this class as a controller for web requests
public class WebController {

    @GetMapping("/") // Maps this method to the home page URL (e.g., http://localhost:8080/)
    public String index(Model model) {
        model.addAttribute("message", "Greetings from Spring Boot!"); // Adds data to the model
        return "index"; // Returns the "index.html" template name
    }
}