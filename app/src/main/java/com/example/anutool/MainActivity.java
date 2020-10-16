package com.example.anutool;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private TextView breakFastView;
    private TextView lunchView;
    private TextView dinnerView;

    private final CrawlingHandler handler = new CrawlingHandler(this);
    private static class CrawlingHandler extends Handler {
        private final WeakReference<MainActivity> weakReference;

        public CrawlingHandler(MainActivity activity){
            this.weakReference = new WeakReference<>(activity);
        }
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                weakReference.get().breakFastView.setText(((String[]) msg.obj)[0]);
                weakReference.get().lunchView.setText(((String[]) msg.obj)[1]);
                weakReference.get().dinnerView.setText(((String[]) msg.obj)[2]);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        breakFastView = (TextView) findViewById(R.id.breakfast);
        lunchView = (TextView) findViewById(R.id.lunch);
        dinnerView = (TextView) findViewById(R.id.dinner);

        GregorianCalendar day = new GregorianCalendar();

        MenuCrawling menuCrawling = new MenuCrawling(day.get(GregorianCalendar.DAY_OF_WEEK) - 1, handler);
        Thread t = new Thread(menuCrawling);
        t.start();

    }
}


