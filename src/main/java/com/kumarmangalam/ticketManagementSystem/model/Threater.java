package com.kumarmangalam.ticketManagementSystem.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Threater {
    String threaterId;
    String threaterName;
    String City;
    String address;
    List<String> screenIds = new ArrayList<>();
    
    public Threater(String threaterName) {
        this.threaterName = threaterName;
        this.threaterId = UUID.randomUUID().toString();
   }
    public String getThreaterId() {
        return threaterId;
    }
    public void setThreaterId(String threaterId) {
        this.threaterId = threaterId;
    }
    public String getCity() {
        return City;
    }
    public void setCity(String city) {
        City = city;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public List<String> getScreenIds() {
        return List.copyOf(screenIds);
    }
    public void setScreenIds(List<String> screenIds) {
        this.screenIds = new ArrayList<>(screenIds);
    }

    public String getthreaterName() {
        return threaterName;
    }

    public void setthreaterName(String threaterName) {
        this.threaterName = threaterName;
    }

    public void addScreenId(String screenId){
        if(this.screenIds==null){
            this.screenIds = new ArrayList<>();
        }
        this.screenIds.add(screenId);
    }    

    public void removeScreenId(String screenId){
        if(this.screenIds!=null)
            this.screenIds.remove(screenId);
    }
}
