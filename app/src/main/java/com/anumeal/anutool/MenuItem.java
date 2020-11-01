package com.anumeal.anutool;

import java.util.ArrayList;

public class MenuItem {
    //private String[] mealTime = new String[6];
    private ArrayList<String> mealTime = new ArrayList<String>();

    public ArrayList<String> getMealTime() {
        return mealTime;
    }

    public void setMealTime(int time, String data) {
        this.mealTime.add(time,data);
    }
    public void setMealTime(String data) {
        this.mealTime.add(data);
    }

    public void sliceString() {
        for (int i = 0; i < mealTime.size(); i++) {
            mealTime.set(i, mealTime.get(i).replaceAll(" ", "\n"));
            mealTime.set(i,mealTime.get(i).replaceAll("/", "\n--------\n"));
        }
    }
}
