package com.kumarmangalam.ticketManagementSystem.model;

import java.util.UUID;

public class Screen {
    String screenId;
    String screenName;
    Integer seatingCapacity;


    public Screen(String screenName) {
        this.screenId = UUID.randomUUID().toString();
        this.screenName = screenName;
    }

    public String getScreenId() {
        return screenId;
    }

    public void setScreenId(String screenId) {
        this.screenId = screenId;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }
}
