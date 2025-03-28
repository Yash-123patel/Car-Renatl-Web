package com.tekworks.rental.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {
	
	@NotNull(message = "User id is required to create order ")
	private Long userId;
	
	@NotNull(message = "Amount is required to create order")
	private Double amount;

}
