package com.kumarmangalam.ticketManagementSystem.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kumarmangalam.ticketManagementSystem.model.Screen;

public class ScreenRepository {
    private final Map<String, Screen> screens = new HashMap<>();

    public synchronized void save(Screen value) {
        screens.put(value.getScreenId(), value);
    }

    public synchronized List<Screen> findAll() {
        return List.copyOf(screens.values());
    }

    public synchronized Screen findById(String id) {
        Screen value = screens.get(id);
        if (value == null) throw new IllegalArgumentException("Screen not found: " + id);
        return value;
    }
}
