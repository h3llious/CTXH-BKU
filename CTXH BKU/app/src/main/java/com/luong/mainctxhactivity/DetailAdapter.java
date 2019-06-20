package com.luong.mainctxhactivity;

import android.content.ReceiverCallNotAllowedException;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    private ArrayList<DetailItem> listItem;

    public DetailAdapter(ArrayList<DetailItem> listItem) {
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.registered_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        DetailItem item = listItem.get(i);
        viewHolder.mssv.setText(item.getMssv());
        viewHolder.name.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, mssv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.registeredName);
            mssv = itemView.findViewById(R.id.registeredMSSV);
        }
    }
}
