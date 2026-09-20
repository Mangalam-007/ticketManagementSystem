package com.kumarmangalam.ticketManagementSystem.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kumarmangalam.ticketManagementSystem.model.Show;

public class ShowRepository {
    private final Map<String, Show> shows = new HashMap<>();

    public synchronized void save(Show value) {
        shows.put(value.getShowId(), value);
    }

    public synchronized List<Show> findAll() {
        return List.copyOf(shows.values());
    }

    public synchronized Show findById(String id) {
        Show value = shows.get(id);
        if (value == null) throw new IllegalArgumentException("Show not found: " + id);
        return value;
    }
}
