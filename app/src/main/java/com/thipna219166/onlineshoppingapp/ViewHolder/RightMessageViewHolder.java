package com.thipna219166.onlineshoppingapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thipna219166.onlineshoppingapp.R;

public class RightMessageViewHolder extends RecyclerView.ViewHolder {
    public TextView contentTextView;
    public TextView timeTextView;
    public RightMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        contentTextView = itemView.findViewById(R.id.tv_message_content);
        timeTextView = itemView.findViewById(R.id.tv_message_datetime);

    }

}
