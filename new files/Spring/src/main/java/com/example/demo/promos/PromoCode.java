package com.example.demo.promos;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

// This is the entitiy that will be representing the oromo code in the system
// This also maps to the promo_code table that is in the database 
@Entity
@Table(name = "promo_codes")
public class PromoCode {

	// The primary key that is auto generated 
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// This is the unique promo code that is entereted by the year
	@Column(nullable = false, unique = true)
	private String code;

	// The percentage discount that is applied 
	@Column(nullable = false)
	private Integer discountPercent;

	// This is what will indicate whether the promo code is active or not 
	@Column(nullable = false)
	private Boolean active;

	// Thi is the timestamp for when the promo code gets created 
	@Column(nullable = false)
	private LocalDateTime createdAt;

	// The default constructor that is required by the JPA 
	public PromoCode() {}

	// This is the getter for the ID 
	public Long getId() {
		return id;
	}

	// This is the getter for the promo code 
	public String getCode() {
		return code;
	}

	// This is the getter for the discount percentage
	public Integer getDiscountPercent() {
		return discountPercent;
	}

	// This is the Getter for the active status 
	public Boolean getActive() {
		return active;
	}

	// This is the getter for the creation of the timestamp 
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	// This is the setter for the promo code 
	public void setCode(String code) {
		this.code = code;
	}

	// This is the setter for the acitve status 
	public void setDiscountPercent(Integer discountPercent) {
		this.discountPercent = discountPercent;
	}

	// This is the setter for the active status 
	public void setActive(Boolean active) {
		this.active = active;
	}

	// This is the setter for the creation of the timestamp
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
