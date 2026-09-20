package com.kumarmangalam.ticketManagementSystem.model;

import java.util.UUID;

public class Movie {
    String movieId;
    String title;
    Double duration;

    
    public Movie(String title) {
        this.title = title;
        this.movieId = UUID.randomUUID().toString();
    }

    public Movie(String title, Double duration){
        this(title);
        this.duration = duration;
    }
    public String getMovieId() {
        return movieId;
    }
    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
}
