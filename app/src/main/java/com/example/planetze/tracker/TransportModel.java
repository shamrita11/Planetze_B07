package com.example.planetze.tracker;

//Presenter?
public class TransportPresenter {

    private String id;
    private String distanceDriven;
    private String transportTime;
    private String distanceWalked;
    private String numFlight;
    private String transportActivity;
    private String transportType;
    private String haul;

    public TransportPresenter() {}

    public TransportPresenter(String id, String distanceDriven, String transportTime,
                              String distanceWalked, String numFlight, String transportActivity,
                              String transportType, String haul) {
        this.id = id;
        this.distanceDriven = distanceDriven;
        this.transportTime = transportTime;
        this.distanceWalked = distanceWalked;
        this.numFlight = numFlight;
        this.transportActivity = transportActivity;
        this.transportType = transportType;
        this.haul = haul;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDistanceDriven() { return distanceDriven; }
    public void setDistanceDriven(String distanceDriven) { this.distanceDriven = distanceDriven; }
    public String getTransportTime() { return transportTime; }
    public void setTransportTime(String transportTime) { this.transportTime = transportTime; }
    public String getDistanceWalked() { return distanceWalked; }
    public void setDistanceWalked(String distanceWalked) { this.distanceWalked = distanceWalked; }
    // TODO: Change it to the correct variable name
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
