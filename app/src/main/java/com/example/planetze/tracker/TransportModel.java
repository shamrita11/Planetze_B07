package com.example.planetze.tracker;

//Presenter?
public class TransportModel {
    private String id;
    private double distanceDriven;
    private double transportTime;
    private double distanceWalked;
    private int numFlight;
    private String transportType;
    private String haul;

    public TransportModel() {}

    public TransportModel(String id, double distanceDriven, String transportType,
                          double transportTime, double distanceWalked, int numFlight,
                          String haul) {
        this.id = id;
        this.distanceDriven = distanceDriven;
        this.transportTime = transportTime;
        this.distanceWalked = distanceWalked;
        this.numFlight = numFlight;
        this.transportType = transportType;
        this.haul = haul;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public double getDistanceDriven() { return distanceDriven; }
    public void setDistanceDriven(double distanceDriven) { this.distanceDriven = distanceDriven; }

    public double getTransportTime() { return transportTime; }
    public void setTransportTime(double transportTime) { this.transportTime = transportTime; }

    public double getDistanceWalked() { return distanceWalked; }
    public void setDistanceWalked(double distanceWalked) { this.distanceWalked = distanceWalked; }

    public int getNumFlight() { return numFlight; }
    public void setNumFlight(int numFlight) { this.numFlight = numFlight; }

    public String getTransportType() { return transportType; }
    public void setTransportType(String transportType) { this.transportType = transportType; }

    public String getHaul() { return haul; }
    public void setHaul(String haul) { this.haul = haul; }
}
