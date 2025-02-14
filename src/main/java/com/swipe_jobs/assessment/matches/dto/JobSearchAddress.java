package com.swipe_jobs.assessment.matches.dto;

public class JobSearchAddress {
    public String unit;
    public int maxJobDistance;
    public double longitude;
    public double latitude;

    public JobSearchAddress(String unit, int maxJobDistance, double longitude, double latitude) {
        this.unit = unit;
        this.maxJobDistance = maxJobDistance;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
