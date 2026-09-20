package com.kumarmangalam.ticketManagementSystem.service;

import com.kumarmangalam.ticketManagementSystem.model.*;
import com.kumarmangalam.ticketManagementSystem.repository.BookingRepository;
import com.kumarmangalam.ticketManagementSystem.repository.ShowRepository;
import com.kumarmangalam.ticketManagementSystem.repository.ScreenRepository;
import java.time.*;
import java.util.*;

public class BookingService {
    private final BookingRepository bookings;
    private final ShowRepository shows;
    private final ScreenRepository screens;
    private final Clock clock;
    private final Duration holdDuration;

    public BookingService(BookingRepository bookings, ShowRepository shows, ScreenRepository screens) {
        this(bookings, shows, screens, Clock.systemDefaultZone(), Duration.ofMinutes(5));
    }

    public BookingService(BookingRepository bookings, ShowRepository shows, ScreenRepository screens,
                          Clock clock, Duration holdDuration) {
        this.bookings = Objects.requireNonNull(bookings);
        this.shows = Objects.requireNonNull(shows);
        this.screens = Objects.requireNonNull(screens);
        this.clock = Objects.requireNonNull(clock);
        this.holdDuration = Objects.requireNonNull(holdDuration);
        if (holdDuration.isZero() || holdDuration.isNegative()) {
            throw new IllegalArgumentException("Hold duration must be positive");
        }
    }

    /** Return available seats for a future show. This does not reserve them. */
    public List<Seat> getAvailableSeats(String showId) {
        synchronized (bookings) {
            expireHolds();
            List<Seat> availableSeats = new ArrayList<>();
            Set<Integer> occupiedSeat = occupiedSeats(showId);
            Show show = upcomingShow(showId);
            Screen screen = screens.findById(show.getScreenId());
            int totalSeats = capacity(show);
            for(int i=1;i<=totalSeats;i++){
                if(!occupiedSeat.contains(i)){
                    availableSeats.add(new Seat(screen.getScreenId(), i));
                }
            }
            return availableSeats;

        }
    }

    /** Reserve all requested seats together, initially in PENDING_PAYMENT state. */
    public Booking createBooking(String userId, String showId, List<Integer> seatNumbers) {
        // TODO: Reject null/blank user IDs and null/empty selections (IllegalArgumentException).
        if(userId==null||showId==null||seatNumbers==null)
            throw new IllegalStateException("User ID, show ID or seat numbers are not defined");
        // TODO: Copy the requested numbers so later caller changes cannot affect this operation.
        Show show = shows.findById(showId);
        String screenId = show.getScreenId();
        synchronized (bookings) {
            // TODO: Expire old holds and validate the show and screen capacity.
            expireHolds();
            // TODO: Reject null, duplicate, or out-of-range numbers (IllegalArgumentException).
            // TODO: Reject the WHOLE request if any seat is occupied (IllegalStateException).
            Set<Integer> occupied = occupiedSeats(showId);
            for(Integer number:seatNumbers){
                if(occupied.contains(number)){
                    throw new IllegalStateException("Seat " + number + " is already occupied");
                }
            }
            // TODO: Read clock.instant(); expiry is the earlier of now + holdDuration and show start.
            Instant now = clock.instant();
            Instant holdExpiry = now.plus(holdDuration);
            Instant showStart = show.getStartTime().atZone(clock.getZone()).toInstant();
            Instant expiresAt = holdExpiry.isBefore(showStart)?holdExpiry:showStart;
            // Hint: convert show start using atZone(clock.getZone()).toInstant().
            // TODO: Build Seats using the screen ID; generate a UUID for the booking.
            List<Seat> seatList = seatNumbers.stream().map(no->new Seat(screenId, no)).toList();
            // TODO: Create, save, and return a PENDING_PAYMENT booking with no payment reference.
            Booking booking = new Booking(UUID.randomUUID().toString(), userId, showId, seatList, BookingStatus.PENDING_PAYMENT, now, expiresAt, null);
            // Keep the availability check AND save inside this same lock.
            bookings.save(booking);
            return booking;
        }
    }

    /** Called after trusted payment verification; do not charge money here. */
    public Booking confirmBooking(String bookingId, String paymentReference) {
        // TODO: Reject a null/blank payment reference (IllegalArgumentException).
        if(bookingId ==null || paymentReference == null)
            throw new IllegalStateException("Booling Id and Payment Reference should hold some value");
        synchronized (bookings) {
            // TODO: Expire old holds and load the booking using bookings.findById().
            expireHolds();
            Booking booking = bookings.findById(bookingId);
            // TODO: If already CONFIRMED with the same reference, return it (safe retry).
            if(booking.getStatus()==BookingStatus.CONFIRMED && Objects.equals(booking.getPaymentReference(),paymentReference)){
                return booking;
            }
            if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
                throw new IllegalStateException("Booking must be pending payment to be confirmed");
            }
            if(booking.getStatus()==BookingStatus.PENDING_PAYMENT){
                Show show = shows.findById(booking.getShowId());
                Instant now = Instant.now(clock);
                Instant startTimeInstant = show.getStartTime().atZone(clock.getZone()).toInstant();
                
                if(now.isBefore(startTimeInstant))
                    booking.setPaymentReference(paymentReference);
                else
                    throw new IllegalStateException("Movie has already been started");
            }
            // TODO: Validate that the show has not started.
            // TODO: Reject a payment reference already used by another booking.
            // TODO: Use withStatus(CONFIRMED, paymentReference), save, and return the new snapshot.
            return booking;
        }
    }

    /** Cancel an unpaid hold. Cancelling a confirmed booking needs a separate refund flow. */
    public Booking cancelBooking(String bookingId) {
        synchronized (bookings) {
            // TODO: Expire holds, then retrieve the booking.
            expireHolds();
            // TODO: Return already CANCELLED or EXPIRED bookings unchanged.
            Booking booking = bookings.findById(bookingId);
            BookingStatus status = booking.getStatus();
            if(status==BookingStatus.CANCELLED || status==BookingStatus.EXPIRED){
                return booking;
            }

            // TODO: Reject CONFIRMED bookings with IllegalStateException.
            if(status == BookingStatus.CONFIRMED)
                throw new IllegalStateException(bookingId + " is already confirmed");
            // TODO: Save and return a CANCELLED snapshot with a null payment reference.
            if(status == BookingStatus.PENDING_PAYMENT){
                booking.setStatus(BookingStatus.CANCELLED);
            }
            bookings.save(booking);
            return booking;
            // Seats become available because occupiedSeats ignores cancelled bookings.
        }
    }

    /** Return the latest booking snapshot, accounting for hold expiry. */
    public Booking getBooking(String bookingId) {
        synchronized (bookings) {
            // TODO: Expire holds, then fetch the booking from the repository.
            throw new UnsupportedOperationException("Implement getBooking");
        }
    }

    /** Called while holding the bookings lock. No background timer is needed. */
    private void expireHolds() {
        Instant now = clock.instant();
        for (Booking booking : bookings.findAll()) {
            if (booking.getStatus() == BookingStatus.PENDING_PAYMENT
                    && !now.isBefore(booking.getExpiresAt())) {         
                bookings.save(booking.withStatus(BookingStatus.EXPIRED, null));
            }
        }
    }

    /** Call after expiry processing and while holding the bookings lock. */
    private Set<Integer> occupiedSeats(String showId) {
        // TODO: Collect seat numbers into a HashSet to avoid duplicates.
        // Include only bookings for this show that are PENDING_PAYMENT or CONFIRMED.
        // A booking for another show must not affect this show's availability.
        Set<Integer> occupiedSeats = new HashSet<Integer>();
        for(Booking booking:bookings.findAll()){
            if(booking.getShowId().equals(showId)){
                if(booking.getStatus().equals(BookingStatus.CONFIRMED)||booking.getStatus().equals(BookingStatus.PENDING_PAYMENT)){
                    booking.getSeats().stream().map(seat->seat.getNumber()).forEach(occupiedSeats::add);

                }
            }
        }
        return occupiedSeats;
    }

    /** Find a show that exists and starts strictly after the current time. */
    private Show upcomingShow(String showId) {
        // TODO: Look up the show; throw IllegalArgumentException if missing.
        Show show = shows.findById(showId);
        if(show==null){
            throw new IllegalStateException("No such show exists");
        }
        Instant showStart = show.getStartTime().atZone(clock.getZone()).toInstant();
        Instant now = clock.instant();
        if(now.isAfter(showStart))
            throw new IllegalStateException("Show has already started");

        return show;
        // TODO: Compare startTime with LocalDateTime.now(clock).
        // Throw IllegalStateException if the show has started, including exact start time.
        // TODO: Return the valid show.
    }

    /** Read and validate the screen's configured seating capacity. */
    private int capacity(Show show) {
        if(show==null)
            throw new IllegalStateException("show is empty");
        Screen screen = screens.findById(show.getScreenId());
        int seatingCapacity = screen.getSeatingCapacity();
        if(seatingCapacity<0)
            throw new IllegalStateException("Seating capacity is less than 0");
        return seatingCapacity;
        // TODO: Read screens.findById(show.getScreenId()).getSeatingCapacity().
        // TODO: Reject null or non-positive capacity with IllegalStateException.
        // TODO: Return the capacity.
    }
}
