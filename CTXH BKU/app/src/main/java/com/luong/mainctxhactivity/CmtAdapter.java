package com.luong.mainctxhactivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CmtAdapter extends RecyclerView.Adapter<CmtAdapter.CmtViewHolder> {

    private List<CmtItem> cmtItemList;

    CmtAdapter(List<CmtItem> cmtItemList) {
        this.cmtItemList = cmtItemList;
    }

    @NonNull
    @Override
    public CmtViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cmt_item, viewGroup, false);
        return new CmtViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CmtViewHolder cmtViewHolder, int i) {
        CmtItem item = cmtItemList.get(i);
        cmtViewHolder.textViewName.setText(item.getName());
        cmtViewHolder.textViewTime.setText(item.getTime());
        cmtViewHolder.textViewComment.setText(item.getComment());
    }

    @Override
    public int getItemCount() {
        if (null == cmtItemList) {
            return 0;
        } else {
            return cmtItemList.size();
        }
    }

    class CmtViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewTime, textViewComment;

        CmtViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.comment_name);
            textViewTime = itemView.findViewById(R.id.comment_time);
            textViewComment = itemView.findViewById(R.id.comment_content);
        }
    }
}
