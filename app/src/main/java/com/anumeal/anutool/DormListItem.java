package com.anumeal.anutool;

public class DormListItem {
    private String date;
    private String menu;

    DormListItem(String date, String menu){
        this.date = date;
        this.menu = menu;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
