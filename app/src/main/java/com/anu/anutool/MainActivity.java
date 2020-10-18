package com.anu.anutool;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private RadioGroup timeGroup;
    private TextView menuView;
    private TextView dateView;
    private String[] dayMenu = new String[3];
    private Thread crawlingThread;
    GregorianCalendar day = new GregorianCalendar();


    private final CrawlingHandler handler = new CrawlingHandler(this);

    private static class CrawlingHandler extends Handler {
        private final WeakReference<MainActivity> weakReference;

        public CrawlingHandler(MainActivity activity) {
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                for (int i = 0; i < 3; i++) {
                    weakReference.get().dayMenu[i] = ((String[]) msg.obj)[i];
                }
                weakReference.get().menuView.setText(weakReference.get().dayMenu[0]);
                weakReference.get().timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch (checkedId) {
                            case R.id.morning:
                                weakReference.get().menuView.setText(weakReference.get().dayMenu[0]);
                                break;
                            case R.id.daytime:
                                weakReference.get().menuView.setText(weakReference.get().dayMenu[1]);
                                break;
                            case R.id.evening:
                                weakReference.get().menuView.setText(weakReference.get().dayMenu[2]);
                                break;
                        }
                    }
                });

            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeGroup = (RadioGroup) findViewById(R.id.timeGroup);
        menuView = (TextView) findViewById(R.id.menu);
        dateView = (TextView) findViewById(R.id.date);
        InternetStateCheck();

        dateView.setText(String.format("%s월 %d일", day.get(GregorianCalendar.MONTH) + 1, day.get(GregorianCalendar.DATE)));

        MenuCrawling menuCrawling;
        if (day.get(GregorianCalendar.DAY_OF_WEEK) == 1) {
            menuCrawling = new MenuCrawling(6, handler);
        } else {
            menuCrawling = new MenuCrawling(day.get(GregorianCalendar.DAY_OF_WEEK) - 2, handler);
        }
        crawlingThread = new Thread(menuCrawling);
        crawlingThread.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void InternetStateCheck() {
        boolean result = false;

        ConnectivityManager checkInternet = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities capabilities = checkInternet.getNetworkCapabilities(checkInternet.getActiveNetwork());
        result = !checkInternet.isActiveNetworkMetered();
        if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) result = false;
        if (result) {
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("인터넷 연결 필요")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);                        // 태스크를 백그라운드로 이동
                            finishAndRemoveTask();                        // 액티비티 종료 + 태스크 리스트에서 지우기
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    }).show();
        }
    }
};

