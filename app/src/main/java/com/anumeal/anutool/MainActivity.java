package com.anumeal.anutool;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RadioGroup timeGroup;
    private ArrayList<String> dayMenu = new ArrayList<String>();// 크롤링 받아오는 변수
    private DormRvAdapter listAdapter;
    private AlertDialog.Builder builder;

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
                weakReference.get().dayMenu = ((ArrayList<String>) msg.obj);
                weakReference.get().setDormList();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton infoButton = (ImageButton)findViewById(R.id.infoButton);
        RecyclerView dormListView = (RecyclerView) findViewById(R.id.rvView);
        timeGroup = (RadioGroup) findViewById(R.id.timeGroup);
        RadioGroup locationGroup = (RadioGroup) findViewById(R.id.locationGroup);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dormListView.setLayoutManager(linearLayoutManager);

        listAdapter = new DormRvAdapter();
        dormListView.setAdapter(listAdapter);

        if (getOnline() == 0) {
            disconnectList();
        } else {
            MenuCrawling menuCrawling;
            if (day.get(GregorianCalendar.DAY_OF_WEEK) == 1) {
                menuCrawling = new MenuCrawling(6, handler);
            } else {
                menuCrawling = new MenuCrawling(day.get(GregorianCalendar.DAY_OF_WEEK) - 2, handler);
            }
            Thread crawlingThread = new Thread(menuCrawling);
            crawlingThread.start();
        }

        locationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.dorm) {
                }//기숙사면 기숙사 식단표 아니면 학식
                else {
                    Toast.makeText(getApplicationContext(), "준비중입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alertSet();
        infoButton.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });

    }



    private void setDormList() {
        if (dayMenu.size() > 3) // 일요일일 땐 다음날 월요일 껄 못 가져와서 하루만 출력
        {
            listAdapter.addItem(String.format(Locale.US, "%s월 %d일", day.get(GregorianCalendar.MONTH) + 1, day.get(GregorianCalendar.DATE)), dayMenu.get(0));
            day.add(GregorianCalendar.DATE, 1);
            listAdapter.addItem(String.format(Locale.US, "%s월 %d일", day.get(GregorianCalendar.MONTH) + 1, day.get(GregorianCalendar.DATE)), dayMenu.get(3));
            //RadioGroup click event
            timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.morning) // 아침
                        listAdapter.changeItem(dayMenu.get(0), dayMenu.get(3));
                    else if (checkedId == R.id.daytime)
                        listAdapter.changeItem(dayMenu.get(1), dayMenu.get(4));
                    else if (checkedId == R.id.evening)
                        listAdapter.changeItem(dayMenu.get(2), dayMenu.get(5));
                    listAdapter.notifyDataSetChanged();
                }
            });
        } else {
            listAdapter.addItem(String.format("%s월 %d일", day.get(GregorianCalendar.MONTH) + 1, day.get(GregorianCalendar.DATE)), dayMenu.get(0));
            //RadioGroup click event
            timeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (checkedId == R.id.morning) // 아침
                        listAdapter.changeItem(dayMenu.get(0), null);
                    else if (checkedId == R.id.daytime)
                        listAdapter.changeItem(dayMenu.get(1), null);
                    else if (checkedId == R.id.evening)
                        listAdapter.changeItem(dayMenu.get(2), null);
                    listAdapter.notifyDataSetChanged();
                }
            });
        }
        listAdapter.notifyDataSetChanged();
    }


    private void disconnectList() {
        listAdapter.addItem(String.format("%s월 %d일", day.get(GregorianCalendar.MONTH) + 1, day.get(GregorianCalendar.DATE)), "인터넷을 연결 후 실행 해주세요.");
        listAdapter.notifyDataSetChanged();
    }
    private void alertSet() {
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("개발자 정보");
        builder.setMessage("이메일: taggma12@gmail.com \n\n생일: 5/1");
        builder.setPositiveButton("Close",null );
        builder.create();
    }

    public int getOnline() {    //출처: https://ariarihan.tistory.com/21 [아리아리한]
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


}

;