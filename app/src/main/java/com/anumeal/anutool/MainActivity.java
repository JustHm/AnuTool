package com.anumeal.anutool;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

        dateView.setText(String.format("%s월 %d일", day.get(GregorianCalendar.MONTH) + 1, day.get(GregorianCalendar.DATE)));

        if (getOnline() == 0) {
            menuView.setText("인터넷을 연결 후 실행 해주세요.");
        }

        MenuCrawling menuCrawling;
        if (day.get(GregorianCalendar.DAY_OF_WEEK) == 1) {
            menuCrawling = new MenuCrawling(6, handler);
        } else {
            menuCrawling = new MenuCrawling(day.get(GregorianCalendar.DAY_OF_WEEK) - 2, handler);
        }
        crawlingThread = new Thread(menuCrawling);
        crawlingThread.start();
    }

    public int getOnline() {
        int ret_code = 0;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnectedOrConnecting()) {
                // wifi 연결중
                return 1;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE && activeNetwork.isConnectedOrConnecting()) {
                // 모바일 네트워크 연결중
                return 2;
            } else {
                // 네트워크 오프라인 상태.
                return 0;
            }
        } else {
            //네트워크 없는 상태
            return 0;
        }
    }


    //출처: https://ariarihan.tistory.com/21 [아리아리한]
};

