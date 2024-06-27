package com.example.cricketscoringapp.Models;

public class Tournament {
    private String id;
    private String name;
    private String location;
    private String startDate;
    private String endDate;
    private String userId;

    // Default constructor required for calls to DataSnapshot.getValue(Tournament.class)
    public Tournament() {
    }

    public Tournament(String id, String name, String location, String startDate, String endDate, String userId) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getUserId() {
        return userId;
    }
}