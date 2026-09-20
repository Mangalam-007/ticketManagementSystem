package com.kumarmangalam.ticketManagementSystem.model;

import java.util.Objects;
import java.time.Instant;
import java.util.List;

public class Booking {
    private String bookingId;
    private String userId;
    private String showId;
    private List<Seat> seats;
    private BookingStatus status;
    private Instant createdAt;
    private Instant expiresAt;
    private String paymentReference;

    public Booking(String bookingId, String userId, String showId, List<Seat> seats, BookingStatus status, Instant createdAt, Instant expiresAt, String paymentReference) {
        setBookingId(bookingId);
        setUserId(userId);
        setShowId(showId);
        setSeats(seats);
        setStatus(status);
        setCreatedAt(createdAt);
        setExpiresAt(expiresAt);
        setPaymentReference(paymentReference);
    }

    public Booking(Booking other) {
        this(other.bookingId, other.userId, other.showId, other.seats, other.status, other.createdAt, other.expiresAt, other.paymentReference);
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public List<Seat> getSeats() {
        return seats.stream().map(Seat::new).toList();
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats.stream().map(Seat::new).toList();
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Booking withStatus(BookingStatus newStatus, String reference) {
        return new Booking(bookingId, userId, showId, seats, newStatus,
                createdAt, expiresAt, reference);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Booking other = (Booking) object;
        return Objects.equals(bookingId, other.bookingId)
                && Objects.equals(userId, other.userId)
                && Objects.equals(showId, other.showId)
                && Objects.equals(seats, other.seats)
                && Objects.equals(status, other.status)
                && Objects.equals(createdAt, other.createdAt)
                && Objects.equals(expiresAt, other.expiresAt)
                && Objects.equals(paymentReference, other.paymentReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId, userId, showId, seats, status, createdAt, expiresAt, paymentReference);
    }

    @Override
    public String toString() {
        return "Booking[" + "bookingId=" + bookingId + ", " + "userId=" + userId + ", " + "showId=" + showId + ", " + "seats=" + seats + ", " + "status=" + status + ", " + "createdAt=" + createdAt + ", " + "expiresAt=" + expiresAt + ", " + "paymentReference=" + paymentReference + "]";
    }
}
