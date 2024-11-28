package com.example.planetze;

public class EcoBalanceProject {
        private String name;
        private String description;
        private String location;
        private String impactMetrics;
        private double cost;


        // Constructor
        public EcoBalanceProject(String name, String description, String location, String impactMetrics, double cost) {
            this.name = name;
            this.description = description;
            this.location = location;
            this.impactMetrics = impactMetrics;
            this.cost = cost;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }
        public String getImpactMetrics() { return impactMetrics; }
        public double getCostPerTon() { return cost; }
    }

