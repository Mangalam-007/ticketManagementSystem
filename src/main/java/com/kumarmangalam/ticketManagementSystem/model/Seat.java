package com.kumarmangalam.ticketManagementSystem.model;

import java.util.Objects;

public class Seat {
    private String screenId;
    private int number;

    public Seat(String screenId, int number) {
        setScreenId(screenId);
        setNumber(number);
    }

    public Seat(Seat other) {
        this(other.screenId, other.number);
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        if (screenId == null || screenId.isBlank()) throw new IllegalArgumentException("A seat needs a screen and a positive number");
        this.screenId = screenId;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        if (number < 1) throw new IllegalArgumentException("A seat needs a screen and a positive number");
        this.number = number;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Seat other = (Seat) object;
        return Objects.equals(screenId, other.screenId)
                && Objects.equals(number, other.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenId, number);
    }

    @Override
    public String toString() {
        return "Seat[" + "screenId=" + screenId + ", " + "number=" + number + "]";
    }
}
