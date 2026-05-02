package com.example.demo.promos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import org.springframework.stereotype.Service;


// This is the service layer and it is responsible for the handling of all of the promo code logic 
// Thi also includes creating the promo codes and validating them during checkout 
@Service
public class PromoService {

	// This is what references the repository for the database operations 
	private final PromoCodeRepository promoCodeRepository;

// This is the constructor injection	
	public PromoService(PromoCodeRepository promoCodeRepository) {
		this.promoCodeRepository = promoCodeRepository;
	}

	// This is what hanles the creation of the acctual promo codes on the admin side 
	public boolean createPromoCode(String rawCode, int discountPercent) {
		
		// This will normalize the code and trim the uppercase for the consistency 
		String code = normalizeCode(rawCode);
		
		// This is just some basic validation the code can't be empty and percent to be reasoanble 
		if (code.isEmpty() || discountPercent < 1 || discountPercent > 90) {
			return false;
		}

		// This will check if the promo code already exist 
		Optional<PromoCode> existing = promoCodeRepository.findByCode(code);
		if (existing.isPresent()) {
			
			// If it does in fact exist it will update the existing promo instead of creating a new one
			PromoCode promoCode = existing.get();
			promoCode.setDiscountPercent(discountPercent);
			promoCode.setActive(true);
			promoCode.setCreatedAt(LocalDateTime.now());
			promoCodeRepository.save(promoCode);
			return true;
		}

		// If the promo does not exist, then it will create a new one 
		PromoCode promoCode = new PromoCode();
		promoCode.setCode(code);
		promoCode.setDiscountPercent(discountPercent);
		promoCode.setActive(true);
		promoCode.setCreatedAt(LocalDateTime.now());
		promoCodeRepository.save(promoCode);
		return true;
	}

	// This is what validates the promo code and it will calculate the discount 
	public PromoValidationResult validatePromo(String rawCode, BigDecimal subtotal) {
		String code = normalizeCode(rawCode);
		
		// This is what will check if the code is empty 
		if (code.isEmpty()) {
			return PromoValidationResult.invalid("Please enter a promo code.");
		}

		// This will ensure that the subtotal is valid and has to be greater than zero 
		if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
			return PromoValidationResult.invalid("Promo code can only be used with ticket purchases.");
		}

		// This will attempt to find the promo inside the database 
		Optional<PromoCode> record = promoCodeRepository.findByCode(code);
		
		// Check if the promo code exist and if it is active 
		if (record.isEmpty() || !Boolean.TRUE.equals(record.get().getActive())) {
			return PromoValidationResult.invalid("Invalid promo code.");
		}

		PromoCode promoCode = record.get();
		
		// This will convert the percent to a decimal 
		BigDecimal discountRate = BigDecimal.valueOf(promoCode.getDiscountPercent()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
		
		// This what will calculate the discount amount 
		BigDecimal discountAmount = subtotal.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
		
		// This what will calculate the subtotal after the discount is applied 
		BigDecimal discountedSubtotal = subtotal.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);

		// This will return the successful validation result 
		return PromoValidationResult.valid(code, promoCode.getDiscountPercent(), discountAmount, discountedSubtotal);
	}

	// The helper method in order to normalize the promo code input 
	// This also ensure that the consistency by trimming and will convert to uppercase 
	private String normalizeCode(String rawCode) {
		if (rawCode == null) {
			return "";
		}
		return rawCode.trim().toUpperCase(Locale.US);
	}

	// Helper class that is used to return the promo validation results
	public static class PromoValidationResult {
		private final boolean valid;
		private final String code;
		private final int percentOff;
		private final BigDecimal discountAmount;
		private final BigDecimal discountedSubtotal;
		private final String message;

		private PromoValidationResult(
			boolean valid,
			String code,
			int percentOff,
			BigDecimal discountAmount,
			BigDecimal discountedSubtotal,
			String message
		) {
			this.valid = valid;
			this.code = code;
			this.percentOff = percentOff;
			this.discountAmount = discountAmount;
			this.discountedSubtotal = discountedSubtotal;
			this.message = message;
		}

		// The factory method for the valid promo results 
		public static PromoValidationResult valid(
			String code,
			int percentOff,
			BigDecimal discountAmount,
			BigDecimal discountedSubtotal
		) {
			return new PromoValidationResult(true, code, percentOff, discountAmount, discountedSubtotal, "Promo applied.");
		}

		// This is the factory method for any invalid promo result 
		public static PromoValidationResult invalid(String message) {
			return new PromoValidationResult(false, "", 0, BigDecimal.ZERO, BigDecimal.ZERO, message);
		}

		// This what will indicate whether the promo is valid 
		public boolean isValid() {
			return valid;
		}

		
		public String getCode() {
			return code;
		}

		public int getPercentOff() {
			return percentOff;
		}

		public BigDecimal getDiscountAmount() {
			return discountAmount;
		}

		public BigDecimal getDiscountedSubtotal() {
			return discountedSubtotal;
		}

		public String getMessage() {
			return message;
		}
	}
}
