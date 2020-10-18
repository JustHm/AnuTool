package com.anu.anutool;

public class MenuItem {
    private String[] mealTime = new String[3];


    public String[] getMealTime() {
        return mealTime;
    }

    public void setMealTime(int time, String data) {
        this.mealTime[time] = data;
    }

    public void sliceString() {
        for (int i = 0; i < 3; i++) {
            mealTime[i] = mealTime[i].replaceAll(" ", "\n");
            mealTime[i] = mealTime[i].replaceAll("/", "\n--------\n");
        }
    }
}
