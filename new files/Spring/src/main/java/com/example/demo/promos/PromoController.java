package com.example.demo.promos;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

// This is the controller that is responsible for handling the promo code related task
// It also includes the creation of promo codes and will also validate them during checkout 
@Controller
@RequestMapping
public class PromoController {

	// This is what will reference to the service layer and where th promo logic is actually handled 
	private final PromoService promoService;

	// CConstructor injection that is for PromoService 
	public PromoController(PromoService promoService) {
		this.promoService = promoService;
	}

	// Create the Promo from admin 

	// This is the endpoint that will be used by the admin to create a new promo code 
	// It takes the promo code and the percentage that is assocaited with it 
	@PostMapping("/admin/promos/create")
	public String createPromo(
		@RequestParam String code,
		@RequestParam Integer percentOff
	) {
		
		// This is the call of service that will create the promo code 
		boolean created = promoService.createPromoCode(code, percentOff);
		if (created) {
			return "redirect:/html/pages/adminpromos.html?status=created";
		}
		
		// If there is an invalid input or also a duplicate, then there will return a error status
		return "redirect:/html/pages/adminpromos.html?status=invalid";
	}

	// This is for validating the promo on the user side 

	// This is the endpoint that will be used during the checkout in order to validate the promo code 
	// It also returns the discount details as a JSON response 
	@GetMapping("/api/promos/validate")
	@ResponseBody
	public Map<String, Object> validatePromo(
		@RequestParam String code,
		@RequestParam BigDecimal subtotal
	) {
		
		// This is the call of serivce in order to validate the promo code and then calculate the discount amount 
		PromoService.PromoValidationResult result = promoService.validatePromo(code, subtotal);

		// This will build the response object to send back to the frontend 
		Map<String, Object> response = new HashMap<>();
		
		// This is what will indicate whether the promo code is valid 
		response.put("valid", result.isValid());
		
		// This is the message explaining result (valid, invalid, expired, etc)
		response.put("message", result.getMessage());
		
		// The promo code being used 
		response.put("code", result.getCode());
		
		// This is the percentage amount that is being actually applied 
		response.put("percentOff", result.getPercentOff());
		
		// This is the calculated amount of the discount 
		response.put("discountAmount", result.getDiscountAmount());
		
		// Then this will be the final subtotal after applying all of the discount
		response.put("discountedSubtotal", result.getDiscountedSubtotal());
		return response;
	}
}
