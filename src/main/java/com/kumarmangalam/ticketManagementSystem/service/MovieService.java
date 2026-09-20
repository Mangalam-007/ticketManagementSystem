package com.kumarmangalam.ticketManagementSystem.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kumarmangalam.ticketManagementSystem.model.Movie;
import com.kumarmangalam.ticketManagementSystem.repository.MovieRepository;

public class MovieService {
    private MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository){
        this.movieRepository = movieRepository;
    }

    public Movie addMovie(String name){
        Movie movie = new Movie(name);
        movieRepository.save(movie);
        return movie;
    }

    public List<Movie> getMovies(){
        return movieRepository.findAll();
    }

    public void getMovie(Movie movie){
        this.movieRepository.save(movie);
    }
}
