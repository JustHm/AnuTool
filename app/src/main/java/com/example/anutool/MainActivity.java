package com.example.anutool;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    //private ArrayList<Class> activities = new ArrayList<>(); //activity 저장

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView breakFastView = (TextView) findViewById(R.id.breakfast);
        TextView lunchView = (TextView) findViewById(R.id.lunch);
        TextView dinnerView = (TextView) findViewById(R.id.dinner);

        //화면 전환을 위한 activity 추가
//        activities.add(SecondActivity.class);
//        activities.add(ThirdActivity.class);
        GregorianCalendar day = new GregorianCalendar();

        MenuCrawling menuCrawling = new MenuCrawling(day.get(GregorianCalendar.DAY_OF_WEEK) - 1);
        Thread t = new Thread(menuCrawling);
        t.start();


        if (menuCrawling.getSize() == 3) // 아,점,저 다 파싱 됐을때
        {
            breakFastView.setText(menuCrawling.getWeekMenu(0).getMealTime()[0]);
            lunchView.setText(menuCrawling.getWeekMenu(1).getMealTime()[1]);
            dinnerView.setText(menuCrawling.getWeekMenu(2).getMealTime()[2]);
        }

    }


//    private void setIcon(){
//        gridAdapter adapter = new gridAdapter(); //adapter로 데이터 세팅해서 추가
//        //data Load
//        TypedArray iconLoad = getResources().obtainTypedArray(R.array.icons);
//        String[] titleLoad = getResources().getStringArray(R.array.iconTitle);
//
//
//        //data settings
//        for(int i = 0; i <iconLoad.length()-1; i++) {
//            adapter.setData(ContextCompat.getDrawable(getApplicationContext(),
//                    iconLoad.getResourceId(i, -1)), titleLoad[i]);
//        }
//        mGridView.setAdapter(adapter);
//
//        //이벤트 설정
//        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, Integer.toString(position), Toast.LENGTH_SHORT).show(); //ToastMsg pop up
//                Intent intent = new Intent(view.getContext(), activities.get(position)); //parameter (context, class of layout)
//                startActivity(intent);
//            }
//        });
//
//
//    }
}