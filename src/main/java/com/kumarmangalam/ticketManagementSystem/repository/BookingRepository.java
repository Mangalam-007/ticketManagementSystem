package com.kumarmangalam.ticketManagementSystem.repository;

import com.kumarmangalam.ticketManagementSystem.model.Booking;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Share one repository across booking services; it is also their transaction lock. */
public class BookingRepository {
    private final Map<String, Booking> bookings = new HashMap<>();

    public synchronized void save(Booking booking) {
        bookings.put(booking.getBookingId(), new Booking(booking));
    }

    public synchronized Booking findById(String id) {
        Booking booking = bookings.get(id);
        if (booking == null) throw new IllegalArgumentException("Booking not found: " + id);
        return new Booking(booking);
    }

    public synchronized List<Booking> findAll() {
        return bookings.values().stream().map(Booking::new).toList();
    }
}
