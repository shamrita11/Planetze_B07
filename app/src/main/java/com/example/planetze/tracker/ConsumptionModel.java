package com.example.planetze.tracker;

public class ConsumptionModel {
    private String id;
    private int numCloth;
    private String deviceType;
    private int numDevice;
    private String purchaseType;
    private int numPurchase;
    private String billType;
    private double bill;

    public ConsumptionModel() {}

    public ConsumptionModel(String id, int numCloth, String deviceType,
                          int numDevice, String purchaseType, int numPurchase,
                          String billType, double bill) {
        this.id = id;
        this.numCloth = numCloth;
        this.deviceType = deviceType;
        this.numDevice = numDevice;
        this.purchaseType = purchaseType;
        this.numPurchase = numPurchase;
        this.billType = billType;
        this.bill = bill;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getNumCloth() { return numCloth; }
    public void setNumCloth(int numCloth) { this.numCloth = numCloth; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public int getNumDevice() { return numDevice; }
    public void setNumDevice(int numDevice) { this.numDevice = numDevice; }

    public String getPurchaseType() { return purchaseType; }
    public void setPurchaseType(String purchaseType) { this.purchaseType = purchaseType; }

    public int getNumPurchase() { return numPurchase; }
    public void setNumPurchase(int numPurchase) { this.numPurchase = numPurchase; }

    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }

    public double getBill() { return bill; }
    public void setBill(double bill) { this.bill = bill; }
}
