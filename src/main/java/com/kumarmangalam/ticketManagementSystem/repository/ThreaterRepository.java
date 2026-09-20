package com.kumarmangalam.ticketManagementSystem.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.kumarmangalam.ticketManagementSystem.model.Threater;

public class ThreaterRepository {
    private final Map<String, Threater> threaters = new HashMap<>();

    public synchronized void save(Threater value) {
        threaters.put(value.getThreaterId(), value);
    }

    public synchronized List<Threater> findAll() {
        return List.copyOf(threaters.values());
    }

    public synchronized Threater findById(String id) {
        Threater value = threaters.get(id);
        if (value == null) throw new IllegalArgumentException("Threater not found: " + id);
        return value;
    }
}
