package com.example.b07demosummer2024;

public class AdoptedHabit {
    private String category;
    private String habit;
    private int daysCompleted;

    public AdoptedHabit(String category, String habit, int daysCompleted) {
        this.category = category;
        this.habit = habit;
        this.daysCompleted = daysCompleted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHabit() {
        return habit;
    }

    public void setHabit(String habit) {
        this.habit = habit;
    }

    public int getDaysCompleted() {
        return daysCompleted;
    }

    public void setDaysCompleted(int daysCompleted) {
        this.daysCompleted = daysCompleted;
    }

}
