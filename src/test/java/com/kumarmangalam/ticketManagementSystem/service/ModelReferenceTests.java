package com.kumarmangalam.ticketManagementSystem.service;

import com.kumarmangalam.ticketManagementSystem.model.*;
import com.kumarmangalam.ticketManagementSystem.repository.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ModelReferenceTests {
    @Test
    void storesIdsFromTheaterThroughShowToBooking() {
        MovieRepository movies = new MovieRepository();
        ShowRepository shows = new ShowRepository();
        ScreenRepository screens = new ScreenRepository();
        ThreaterService theaters = new ThreaterService(new ThreaterRepository(), screens);
        Threater theater = theaters.addThreater("Example");
        Screen screen = theaters.addScreen(theater.getThreaterId(), "Screen 1");
        screen.setSeatingCapacity(10);
        Movie movie = new Movie("Example", 150.0);
        movies.save(movie);
        assertNotNull(movie.getMovieId());
        assertEquals(List.of(screen.getScreenId()), theater.getScreenIds());
        ShowService showService = new ShowService(shows, screens, movies);
        Show show = showService.addShow(screen.getScreenId(), movie.getMovieId(), LocalDateTime.now().plusDays(1));
        assertEquals(movie.getMovieId(), show.getMovieId());
        assertSame(movie, movies.findById(show.getMovieId()));
        assertEquals(screen.getScreenId(), show.getScreenId());
        assertSame(screen, screens.findById(show.getScreenId()));
        BookingService bookingService = new BookingService(new BookingRepository(), shows, screens);
        Booking booking = bookingService.createBooking("user", show.getShowId(), List.of(1));
        assertEquals(List.of(new Seat(screen.getScreenId(), 1)), booking.getSeats());
        assertEquals(9, bookingService.getAvailableSeats(show.getShowId()).size());
    }
}
