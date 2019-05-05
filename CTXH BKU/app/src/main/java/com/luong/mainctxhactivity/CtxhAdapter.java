package com.luong.mainctxhactivity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.luong.mainctxhactivity.CtxhItem;
import com.luong.mainctxhactivity.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CtxhAdapter extends RecyclerView.Adapter<CtxhAdapter.ViewHolder> {
    ArrayList<CtxhItem> ctxhList;
    Context context;

    public CtxhAdapter(ArrayList<CtxhItem> ctxhList, Context context) {
        this.ctxhList = ctxhList;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = layoutInflater.inflate(R.layout.ctxh_item, viewGroup, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i) {
        CtxhItem item = ctxhList.get(i);
        // item.setImg(viewHolder.img);
        Picasso.get().load(item.getImgURL()).into(viewHolder.img);

        viewHolder.deadline.setText(item.getDeadline_register());
        viewHolder.title.setText(item.getTitle());
        viewHolder.start.setText(item.getTime_start());
        viewHolder.end.setText(item.getTime_end());
        viewHolder.ctxh_day.setText(Double.toString(item.getDay_of_ctxh()));
    }

    @Override
    public int getItemCount() {
        return ctxhList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView deadline;
        TextView title;
        TextView start;
        TextView end;
        TextView ctxh_day;


        public ViewHolder(View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.image);
            deadline = itemView.findViewById(R.id.deadline_ctxh);
            title = itemView.findViewById(R.id.title_item);
            start = itemView.findViewById(R.id.thoi_gian_thuchien);
            end = itemView.findViewById(R.id.thoi_gian_ketthuc_thuchien);
            ctxh_day = itemView.findViewById(R.id.day_of_ctxh);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}