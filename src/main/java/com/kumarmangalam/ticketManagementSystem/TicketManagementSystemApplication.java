package com.kumarmangalam.ticketManagementSystem;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kumarmangalam.ticketManagementSystem.model.*;
import com.kumarmangalam.ticketManagementSystem.repository.*;
import com.kumarmangalam.ticketManagementSystem.service.*;
import com.kumarmangalam.ticketManagementSystem.service.payment.*;

@SpringBootApplication
public class TicketManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TicketManagementSystemApplication.class, args);

		MovieRepository movieRepository = new MovieRepository();
		ShowRepository showRepository = new ShowRepository();
		ThreaterRepository threaterRepository = new ThreaterRepository();
		ScreenRepository screenRepository = new ScreenRepository();
		BookingRepository bookingRepository = new BookingRepository();

		MovieService movieService = new MovieService(movieRepository);
		ThreaterService threaterService = new ThreaterService(threaterRepository, screenRepository);
		ShowService showService = new ShowService(showRepository, screenRepository, movieRepository);
		BookingService bookingService = new BookingService(bookingRepository, showRepository, screenRepository);

		Movie movie1 = movieService.addMovie("Hanuman Ansh");
		Movie movie2 = movieService.addMovie("Awarapan 2");
		
		Threater threater1 = threaterService.addThreater("PVR Icon Hitech City");
		Threater threater2 = threaterService.addThreater("Aparna Nallagandla");
		
		Screen screen1 = threaterService.addScreen(threater1.getThreaterId(), "Screen1");
		screen1.setSeatingCapacity(100);
		
		Show show1 = showService.addShow(screen1.getScreenId(), movie1.getMovieId(), LocalDateTime.now().plusDays(1));
		
        Booking booking = bookingService.createBooking("demo-user", show1.getShowId(), java.util.List.of(1, 2));
        System.out.println("Seats held: " + booking);
        // Demo only: a real payment integration must verify payment before confirming.
        PaymentService strategy = new UPIPaymentService();
		PaymentProcessor processor = new PaymentProcessor(strategy);
		if(processor.pay()){
		Booking confirmed = bookingService.confirmBooking(booking.getBookingId(), "demo-payment-001");
        System.out.println("Booking confirmed: " + confirmed);
		}
		
	}
}
