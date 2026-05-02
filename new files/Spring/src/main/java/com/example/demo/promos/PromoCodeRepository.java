package com.example.demo.promos;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// This is the repository that is used for the promo code database operations 
// This also extends the JpaRepository in order to provide the built in CRUD operations 
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
	
	// This can find the promo code by its value 
	Optional<PromoCode> findByCode(String code);
}
