package com.kumarmangalam.ticketManagementSystem.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Show {
    String showId;
    String screenId;
    String movieId;
    LocalDateTime startTime;
    LocalDateTime endTime;

    public Show(String screenId, String movieId, LocalDateTime startTime) {
        this.showId = UUID.randomUUID().toString();
        this.screenId = screenId;
        this.movieId = movieId;
        this.startTime = startTime;
        this.endTime = startTime.plusHours(2).plusMinutes(30);
    }

    public String getShowId() {
        return showId;
    }
    public void setShowId(String showId) {
        this.showId = showId;
    }
    public String getScreenId() {
        return screenId;
    }
    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }
    public String getMovieId() {
        return movieId;
    }
    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    @Override
    public String toString() {
        return "Show [showId=" + showId + ", screenId=" + screenId + ", movieId=" + movieId + ", startTime=" + startTime
                + ", endTime=" + endTime + "]";
    }
    
}
