package com.tekworks.rental.entity;

import java.time.Instant;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingPayment {

	@Id
	private String orderId;
	private Double amount;
	private String currency;
	private Instant orderDate;
	private Boolean isOrderActive;
	private String paymentStatus;
	private Instant createdAt;
	private Instant updatedAt;

	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private Users user;

}
