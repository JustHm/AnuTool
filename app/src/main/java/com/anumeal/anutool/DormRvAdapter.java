package com.anumeal.anutool;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DormRvAdapter extends RecyclerView.Adapter<DormRvAdapter.DormRvHolder> {
    ArrayList<DormListItem> dormItem = new ArrayList<DormListItem>();


    @NonNull
    @Override
    public DormRvHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_list, parent, false);
        return new DormRvHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DormRvHolder holder, int position) {
        holder.onBind(dormItem.get(position));
    }

    @Override
    public int getItemCount() {
        return dormItem.size();
    }

    public void addItem(String date, String menu){
        DormListItem temp = new DormListItem(date, menu);
        dormItem.add(temp);
    }
    public void changeItem(String today, String nextDay){
        dormItem.get(0).setMenu(today);
        if(nextDay != null)
            dormItem.get(1).setMenu(nextDay);
    }

    public class DormRvHolder extends RecyclerView.ViewHolder {
        private TextView dateView;
        private TextView menuView;
        public DormRvHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.date);
            menuView = itemView.findViewById(R.id.menu);
        }

        void onBind(DormListItem data){
            dateView.setText(data.getDate());
            menuView.setText(data.getMenu());
        }
    }
}
