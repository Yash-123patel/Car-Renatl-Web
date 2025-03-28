package com.tekworks.rental.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tekworks.rental.dto.BookingHistoryDto;
import com.tekworks.rental.dto.BookingStatusUpdate;
import com.tekworks.rental.entity.BookingHistory;
import com.tekworks.rental.response.ErrorResponse;
import com.tekworks.rental.service.BookingHistoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/booking")
public class BookingHistoryController {

	@Autowired
	private BookingHistoryService bookingHistoryService;

	@PostMapping("/bookCar/{userId}/{carId}")
	public ResponseEntity<?> bookCar(@Valid @RequestBody BookingHistoryDto bookingHistoryDto, @PathVariable Long userId,
			@PathVariable Long carId) {

		try {

			bookingHistoryDto.setUserId(userId);
			bookingHistoryDto.setCarId(carId);
			bookingHistoryService.bookCar(bookingHistoryDto);
			return ResponseEntity.ok("Car booked Successfully");

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
		}

		catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}

	}

	@GetMapping("/getUpcomingBookings/{userId}")
	public ResponseEntity<?> getUpcomingBookings(@PathVariable Long userId) {

		try {

			List<BookingHistoryDto> upcomingBooking = bookingHistoryService.getUpcomingBooking(userId);
			return ResponseEntity.ok(upcomingBooking);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
	}

	@GetMapping("/getCompleteBookings/{userId}")
	public ResponseEntity<?> getCompleteBookings(@PathVariable Long userId) {

		try {

			List<BookingHistoryDto> upcomingBooking = bookingHistoryService.getCompleteBooking(userId);
			return ResponseEntity.ok(upcomingBooking);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
	}

	@GetMapping("/getCancledBookings/{userId}")
	public ResponseEntity<?> getCancledBookings(@PathVariable Long userId) {

		try {

			List<BookingHistoryDto> upcomingBooking = bookingHistoryService.getCancleBooking(userId);
			return ResponseEntity.ok(upcomingBooking);

		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
	}

	@PutMapping("/updateBookingStatus/{userId}")
	public ResponseEntity<?> updateBookingStatus(@Valid @RequestBody BookingStatusUpdate bookingStatusUpdate,
			@PathVariable Long userId) {
		
		try {
			if(!(bookingStatusUpdate.getNewStatus().equalsIgnoreCase("completed")||bookingStatusUpdate.getNewStatus().equalsIgnoreCase("cancled"))) {
				return ResponseEntity.badRequest().body("Invalid new status only ['completed || cancled ']");
			}
			
			BookingHistory updateBookingStatus = bookingHistoryService.updateBookingStatus(bookingStatusUpdate, userId);
			
			if(updateBookingStatus==null) {
	            throw new RuntimeException("Error while updating status");
			}
			return ResponseEntity.ok("Booking status updated successfully");
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), Instant.now()));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", Instant.now()));
		}
		
	}
	
}
