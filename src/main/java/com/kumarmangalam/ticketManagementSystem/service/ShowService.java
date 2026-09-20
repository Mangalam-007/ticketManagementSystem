package com.kumarmangalam.ticketManagementSystem.service;

import java.time.LocalDateTime;
import java.util.List;

import com.kumarmangalam.ticketManagementSystem.model.*;
import com.kumarmangalam.ticketManagementSystem.repository.*;

public class ShowService {
    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final MovieRepository movieRepository;

    public ShowService(ShowRepository showRepository, ScreenRepository screenRepository, MovieRepository movieRepository){
        this.showRepository = showRepository;
        this.screenRepository = screenRepository;
        this.movieRepository = movieRepository;
    }

    public Show addShow(String screenId, String movieId, LocalDateTime startTime){
        screenRepository.findById(screenId);
        movieRepository.findById(movieId);
        List<Show> shows = findShows();
        for(Show show:shows){
            if(show.getScreenId().equals(screenId)&&startTime.isAfter(show.getStartTime()) && startTime.isBefore(show.getEndTime())){
                throw new IllegalStateException("Show is not empty");
            }
        }
        Show show = new Show(screenId, movieId, startTime);
        showRepository.save(show);
        return show;
    }

    public List<Show> findShows(){
        return showRepository.findAll();
    }
}
