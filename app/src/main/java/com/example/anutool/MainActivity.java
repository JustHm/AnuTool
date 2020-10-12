package com.example.anutool;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private GridView mGridView;
    private ArrayList<Class> activities = new ArrayList<>(); //activity 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //화면 전환을 위한 activity 추가
        activities.add(SecondActivity.class);
        activities.add(ThirdActivity.class);

        MenuCrawling menuCrawling = new MenuCrawling();
        Thread t = new Thread(menuCrawling);
        t.start();

        mGridView = (GridView) findViewById(R.id.gView);

        setIcon();
    }


    private void setIcon(){
        gridAdapter adapter = new gridAdapter(); //adapter로 데이터 세팅해서 추가
        //data Load
        TypedArray iconLoad = getResources().obtainTypedArray(R.array.icons);
        String[] titleLoad = getResources().getStringArray(R.array.iconTitle);


        //data settings
        for(int i = 0; i <iconLoad.length()-1; i++) {
            adapter.setData(ContextCompat.getDrawable(getApplicationContext(),
                    iconLoad.getResourceId(i, -1)), titleLoad[i]);
        }
        mGridView.setAdapter(adapter);

        //이벤트 설정
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show(); //ToastMsg pop up
                Intent intent = new Intent(view.getContext(), activities.get(position)); //parameter (context, class of layout)
                startActivity(intent);
            }
        });


    }
}