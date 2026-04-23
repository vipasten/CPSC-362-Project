package com.example.demo.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface UserRepository extends JpaRepository<UserAccount, Long> {
	Optional<UserAccount> findByUsername(String username);
	List<UserAccount> findByUsernameContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String username, String firstName, String lastName);
	
}
