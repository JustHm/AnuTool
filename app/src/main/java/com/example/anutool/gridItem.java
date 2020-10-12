package com.example.anutool;

import android.graphics.drawable.Drawable;

import org.w3c.dom.Text;

public class gridItem {
    private Drawable img;
    private String t;


    public void setItem(Drawable d, String s){
        this.img=d;
        this.t=s;
    }

    public String getT() {
        return t;
    }

    public Drawable getImg() {
        return img;
    }
}
