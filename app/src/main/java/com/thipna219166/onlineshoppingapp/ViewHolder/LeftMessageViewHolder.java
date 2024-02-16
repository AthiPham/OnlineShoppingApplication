package com.thipna219166.onlineshoppingapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thipna219166.onlineshoppingapp.R;

public class LeftMessageViewHolder extends RecyclerView.ViewHolder {
    public TextView nameTextView, contentTextView,timeTextView;
    public ImageView imageView;

    public LeftMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.tv_user_name);
        contentTextView = itemView.findViewById(R.id.tv_message_content);
        timeTextView = itemView.findViewById(R.id.tv_message_datetime);
        imageView = itemView.findViewById(R.id.image_user);
    }

}
