package com.kumarmangalam.ticketManagementSystem.controller;

import com.kumarmangalam.ticketManagementSystem.model.Movie;
import com.kumarmangalam.ticketManagementSystem.service.MovieService;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MovieController {
    private final MovieService movies;

    public MovieController(MovieService movies) {
        this.movies = movies;
    }

    @GetMapping("/api/movies")
    public List<Movie> getMovies() {
        return movies.getMovies().stream()
                .sorted(Comparator.comparing(Movie::getTitle, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
