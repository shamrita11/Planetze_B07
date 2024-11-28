package com.example.planetze.tracker;

public class FoodModel {
    private String id;
    private int numServing;
    private String foodType;

    public FoodModel() {}

    public FoodModel(String id, int numServing, String foodType) {
        this.id = id;
        this.numServing = numServing;
        this.foodType = foodType;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getNumServing() { return numServing; }
    public void setDistanceDriven(int numServing) { this.numServing = numServing; }

    public String getTransportTime() { return foodType; }
    public void setTransportTime(String foodType) { this.foodType = foodType; }

}
