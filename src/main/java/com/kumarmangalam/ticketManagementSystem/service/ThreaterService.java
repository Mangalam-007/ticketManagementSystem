package com.kumarmangalam.ticketManagementSystem.service;

import java.util.List;

import com.kumarmangalam.ticketManagementSystem.model.Screen;
import com.kumarmangalam.ticketManagementSystem.model.Threater;
import com.kumarmangalam.ticketManagementSystem.repository.ThreaterRepository;
import com.kumarmangalam.ticketManagementSystem.repository.ScreenRepository;

public class ThreaterService {
    private final ThreaterRepository threaterRepository;
    private final ScreenRepository screenRepository;

    public ThreaterService(ThreaterRepository threaterRepository, ScreenRepository screenRepository){
        this.threaterRepository = threaterRepository;
        this.screenRepository = screenRepository;
    }
    
    public Threater addThreater (String threaterName){
        Threater threater = new Threater(threaterName);
        threaterRepository.save(threater);
        return threater;
    }

    public List<Threater> getThreaters(){
        return threaterRepository.findAll();
    }

    public Screen addScreen(String threaterId, String screenName){
        Threater threater = threaterRepository.findById(threaterId);
        Screen screen = new Screen(screenName);
        screenRepository.save(screen);
        threater.addScreenId(screen.getScreenId());
        threaterRepository.save(threater);
        return screen;
    }
}
