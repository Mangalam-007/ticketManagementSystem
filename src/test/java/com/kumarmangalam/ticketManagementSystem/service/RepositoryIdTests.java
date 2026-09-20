package com.kumarmangalam.ticketManagementSystem.service;

import com.kumarmangalam.ticketManagementSystem.model.*;
import com.kumarmangalam.ticketManagementSystem.repository.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryIdTests {
    @Test
    void repositoriesReplaceExistingIdsAndRejectMissingIds() {
        MovieRepository movies = new MovieRepository();
        Movie movie = new Movie("First");
        Movie updatedMovie = new Movie("Updated");
        updatedMovie.setMovieId(movie.getMovieId());
        checkReplacement(movie, updatedMovie, movie.getMovieId(), movies::save, movies::findById, movies::findAll);

        ScreenRepository screens = new ScreenRepository();
        Screen screen = new Screen("First");
        Screen updatedScreen = new Screen("Updated");
        updatedScreen.setScreenId(screen.getScreenId());
        checkReplacement(screen, updatedScreen, screen.getScreenId(), screens::save, screens::findById, screens::findAll);

        ShowRepository shows = new ShowRepository();
        Show show = new Show(screen.getScreenId(), movie.getMovieId(), LocalDateTime.now());
        Show updatedShow = new Show(screen.getScreenId(), movie.getMovieId(), LocalDateTime.now().plusDays(1));
        updatedShow.setShowId(show.getShowId());
        checkReplacement(show, updatedShow, show.getShowId(), shows::save, shows::findById, shows::findAll);

        ThreaterRepository theaters = new ThreaterRepository();
        Threater theater = new Threater("First");
        Threater updatedTheater = new Threater("Updated");
        updatedTheater.setThreaterId(theater.getThreaterId());
        checkReplacement(theater, updatedTheater, theater.getThreaterId(), theaters::save, theaters::findById, theaters::findAll);

        BookingRepository bookings = new BookingRepository();
        Booking booking = new Booking("booking", "user", show.getShowId(),
                List.of(new Seat(screen.getScreenId(), 1)), BookingStatus.PENDING_PAYMENT,
                Instant.EPOCH, Instant.EPOCH.plusSeconds(300), null);
        Booking updatedBooking = booking.withStatus(BookingStatus.CONFIRMED, "payment");
        checkReplacement(booking, updatedBooking, booking.getBookingId(), bookings::save, bookings::findById, bookings::findAll);
        updatedBooking.setStatus(BookingStatus.CANCELLED);
        assertEquals(BookingStatus.CONFIRMED, bookings.findById("booking").getStatus());
    }

    private <T> void checkReplacement(T original, T replacement, String id,
            Consumer<T> save, Function<String, T> find, Supplier<List<T>> all) {
        save.accept(original);
        List<T> snapshot = all.get();
        save.accept(replacement);
        assertEquals(replacement, find.apply(id));
        assertEquals(1, all.get().size());
        assertEquals(original, snapshot.get(0));
        assertThrows(IllegalArgumentException.class, () -> find.apply("missing"));
    }

    @Test
    void resolvesTheaterAndShowRelationshipsById() {
        ThreaterRepository theaters = new ThreaterRepository();
        ScreenRepository screens = new ScreenRepository();
        MovieRepository movies = new MovieRepository();
        ShowRepository shows = new ShowRepository();
        ThreaterService theaterService = new ThreaterService(theaters, screens);
        Threater theater = theaterService.addThreater("Cinema");
        Screen screen = theaterService.addScreen(theater.getThreaterId(), "Screen 1");
        Movie movie = new Movie("Movie");
        movies.save(movie);
        ShowService showService = new ShowService(shows, screens, movies);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        Show show = showService.addShow(screen.getScreenId(), movie.getMovieId(), start);
        assertEquals(List.of(screen.getScreenId()), theater.getScreenIds());
        assertSame(screen, screens.findById(show.getScreenId()));
        assertSame(movie, movies.findById(show.getMovieId()));
        assertThrows(IllegalArgumentException.class,
                () -> showService.addShow("missing", movie.getMovieId(), start));
        assertThrows(IllegalArgumentException.class,
                () -> showService.addShow(screen.getScreenId(), "missing", start));
        assertThrows(IllegalArgumentException.class,
                () -> theaterService.addScreen("missing", "Screen 2"));
        assertEquals(1, screens.findAll().size());
        assertEquals(1, shows.findAll().size());
    }
}
