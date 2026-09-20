package com.kumarmangalam.ticketManagementSystem.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kumarmangalam.ticketManagementSystem.model.Movie;

public class MovieRepository {
    private final Map<String, Movie> movies = new HashMap<>();

    public synchronized void save(Movie value) {
        movies.put(value.getMovieId(), value);
    }

    public synchronized List<Movie> findAll() {
        return List.copyOf(movies.values());
    }

    public synchronized Movie findById(String id) {
        Movie value = movies.get(id);
        if (value == null) throw new IllegalArgumentException("Movie not found: " + id);
        return value;
    }
}
