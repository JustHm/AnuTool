package com.example.anutool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class gridAdapter extends BaseAdapter {
    private ArrayList<gridItem> icon = new ArrayList<>();

    @Override
    public int getCount() {
        return icon.size();
    }

    @Override
    public gridItem getItem(int position) {
        return icon.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override// setData 호출하면서 icon 변수에 add 해 줄때마다 호출 됨
    public View getView(int position, View convertView, ViewGroup parent) {

        Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //화면을 겹쳐주기위한 작업
            convertView = inflater.inflate(R.layout.grid_view, parent, false);
        }

        ImageView iconImage = convertView.findViewById(R.id.gImg); // layout view 연결작업
        TextView textView = convertView.findViewById(R.id.desc);

        gridItem dest = getItem(position);


        iconImage.setImageDrawable(dest.getImg());
        textView.setText(dest.getT());

        return convertView;
    }

    public void setData(Drawable d,String string){
        gridItem temp = new gridItem();
        temp.setItem(d,string);
        icon.add(temp);
    }
}
