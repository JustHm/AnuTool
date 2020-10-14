package com.example.anutool;

public class MenuItem {
    private String[] mealTime = new String[3];


    public String[] getMealTime() {
        return mealTime;
    }

    public void setMealTime(int time, String data) {
        this.mealTime[time] = data;
    }
}
