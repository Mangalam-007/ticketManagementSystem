package com.kumarmangalam.ticketManagementSystem.service;

import com.kumarmangalam.ticketManagementSystem.model.*;
import com.kumarmangalam.ticketManagementSystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.List;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class BookingServiceTests {
    private final Instant now = Instant.parse("2026-09-13T10:00:00Z");
    private BookingRepository bookings;
    private ShowRepository shows;
    private ScreenRepository screens;
    private BookingService service;
    private Show show;

    @BeforeEach
    void setUp() {
        bookings = new BookingRepository();
        shows = new ShowRepository();
        screens = new ScreenRepository();
        Screen screen = new Screen("Screen 1");
        screen.setSeatingCapacity(3);
        screens.save(screen);
        show = new Show(screen.getScreenId(), new Movie("Example").getMovieId(), LocalDateTime.ofInstant(now.plusSeconds(3600), ZoneOffset.UTC));
        shows.save(show);
        service = serviceAt(now);
    }

    private BookingService serviceAt(Instant instant) {
        return new BookingService(bookings, shows, screens, Clock.fixed(instant, ZoneOffset.UTC), Duration.ofMinutes(5));
    }

    @Test
    void holdsSeatsThenConfirmsAndSupportsPaymentRetries() {
        Booking held = service.createBooking("user", show.getShowId(), List.of(1, 2));
        assertEquals(BookingStatus.PENDING_PAYMENT, held.getStatus());
        assertEquals(List.of(new Seat(show.getScreenId(), 3)), service.getAvailableSeats(show.getShowId()));
        Booking confirmed = service.confirmBooking(held.getBookingId(), "payment-1");
        assertEquals(BookingStatus.CONFIRMED, confirmed.getStatus());
        assertEquals(confirmed, service.confirmBooking(held.getBookingId(), "payment-1"));
        assertThrows(IllegalStateException.class, () -> service.confirmBooking(held.getBookingId(), "payment-2"));
        assertEquals(1, serviceAt(now.plusSeconds(600)).getAvailableSeats(show.getShowId()).size());
        assertThrows(IllegalStateException.class, () -> service.cancelBooking(held.getBookingId()));
    }

    @Test
    void conflictingMultiSeatRequestDoesNotPartiallyReserve() {
        service.createBooking("first", show.getShowId(), List.of(1));
        assertThrows(IllegalStateException.class, () -> service.createBooking("second", show.getShowId(), List.of(1, 2)));
        assertEquals(2, service.getAvailableSeats(show.getShowId()).size());
    }

    @Test
    void samePhysicalSeatCanBeBookedForDifferentShows() {
        Show later = new Show(show.getScreenId(), show.getMovieId(), show.getStartTime().plusHours(3));
        shows.save(later);
        service.createBooking("first", show.getShowId(), List.of(1));
        assertDoesNotThrow(() -> service.createBooking("second", later.getShowId(), List.of(1)));
    }

    @Test
    void cancellationReleasesSeatsAndCannotBeConfirmed() {
        Booking held = service.createBooking("user", show.getShowId(), List.of(1));
        assertEquals(BookingStatus.CANCELLED, service.cancelBooking(held.getBookingId()).getStatus());
        assertEquals(BookingStatus.CANCELLED, service.cancelBooking(held.getBookingId()).getStatus());
        assertEquals(3, service.getAvailableSeats(show.getShowId()).size());
        assertThrows(IllegalStateException.class, () -> service.confirmBooking(held.getBookingId(), "payment"));
    }

    @Test
    void expiryAtDeadlineReleasesSeatsAndRejectsLateConfirmation() {
        Booking held = service.createBooking("user", show.getShowId(), List.of(1));
        BookingService later = serviceAt(now.plusSeconds(300));
        assertEquals(BookingStatus.EXPIRED, later.getBooking(held.getBookingId()).getStatus());
        assertEquals(3, later.getAvailableSeats(show.getShowId()).size());
        assertThrows(IllegalStateException.class, () -> later.confirmBooking(held.getBookingId(), "late-payment"));
        assertDoesNotThrow(() -> later.createBooking("next", show.getShowId(), List.of(1)));
    }

    @Test
    void rejectsInvalidRequestsAndReusedPaymentReferences() {
        assertThrows(IllegalArgumentException.class, () -> service.createBooking("", show.getShowId(), List.of(1)));
        assertThrows(IllegalArgumentException.class, () -> service.createBooking("user", "missing", List.of(1)));
        for (List<Integer> invalid : List.of(List.<Integer>of(), List.of(0), List.of(4), List.of(1, 1))) {
            assertThrows(IllegalArgumentException.class, () -> service.createBooking("user", show.getShowId(), invalid));
        }
        Booking first = service.createBooking("first", show.getShowId(), List.of(1));
        service.confirmBooking(first.getBookingId(), "payment");
        Booking second = service.createBooking("second", show.getShowId(), List.of(2));
        assertThrows(IllegalStateException.class, () -> service.confirmBooking(second.getBookingId(), "payment"));
        assertEquals(BookingStatus.PENDING_PAYMENT, service.getBooking(second.getBookingId()).getStatus());
    }

    @Test
    void requiresCapacityAndClosesBookingsAtShowStart() {
        screens.findById(show.getScreenId()).setSeatingCapacity(null);
        assertThrows(IllegalStateException.class, () -> service.getAvailableSeats(show.getShowId()));
        screens.findById(show.getScreenId()).setSeatingCapacity(3);
        BookingService beforeStart = serviceAt(now.plusSeconds(3540));
        Booking held = beforeStart.createBooking("user", show.getShowId(), List.of(1));
        assertEquals(now.plusSeconds(3600), held.getExpiresAt());
        BookingService atStart = serviceAt(now.plusSeconds(3600));
        assertThrows(IllegalStateException.class, () -> atStart.createBooking("user", show.getShowId(), List.of(2)));
        assertThrows(IllegalStateException.class, () -> atStart.confirmBooking(held.getBookingId(), "payment"));
    }

    @Test
    void editingReturnedModelsDoesNotChangeStoredReservations() {
        Booking held = service.createBooking("user", show.getShowId(), List.of(1));
        String id = held.getBookingId();
        held.setStatus(BookingStatus.CANCELLED);
        held.setSeats(List.of(new Seat(show.getScreenId(), 2)));
        Booking stored = service.getBooking(id);
        stored.setUserId("another-user");
        assertThrows(UnsupportedOperationException.class, () -> stored.getSeats().clear());
        assertEquals(BookingStatus.PENDING_PAYMENT, service.getBooking(id).getStatus());
        assertEquals("user", service.getBooking(id).getUserId());
        assertEquals(new Seat(show.getScreenId(), 1), service.getBooking(id).getSeats().get(0));
        assertThrows(IllegalStateException.class,
                () -> service.createBooking("other", show.getShowId(), List.of(1)));
    }

    @Test
    void competingServicesAllowOnlyOneSeatHold() throws Exception {
        BookingService other = serviceAt(now);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> first = attempt(service, ready, start);
            Callable<Boolean> second = attempt(other, ready, start);
            Future<Boolean> a = executor.submit(first);
            Future<Boolean> b = executor.submit(second);
            boolean bothReady = ready.await(5, TimeUnit.SECONDS);
            start.countDown();
            assertTrue(bothReady);
            assertNotEquals(a.get(5, TimeUnit.SECONDS), b.get(5, TimeUnit.SECONDS));
            assertEquals(1, bookings.findAll().size());
        }
    }

    private Callable<Boolean> attempt(BookingService target, CountDownLatch ready, CountDownLatch start) {
        return () -> {
            ready.countDown();
            if (!start.await(5, TimeUnit.SECONDS)) throw new AssertionError("Start timed out");
            try {
                target.createBooking("user", show.getShowId(), List.of(1));
                return true;
            } catch (IllegalStateException conflict) {
                return false;
            }
        };
    }
}
