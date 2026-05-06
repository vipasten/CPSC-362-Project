package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// This will be the main entry point for the Spring Boot Application
// This class is what starts the back end server and will intialize all the componenets 
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		// This bootstraps the Spring application and then starts the embedded server 
		SpringApplication.run(DemoApplication.class, args);
	}}

