package com.thipna219166.onlineshoppingapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thipna219166.onlineshoppingapp.Interface.ItemClickListner;
import com.thipna219166.onlineshoppingapp.R;


public class UserViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
    public TextView nameTextView,phoneTextView,passwordTextView;
    public ItemClickListner mItemClickListner;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView=itemView.findViewById(R.id.user_name_tv);
        phoneTextView=itemView.findViewById(R.id.user_phone_tv);
        passwordTextView=itemView.findViewById(R.id.user_password_tv);
    }

    public void setOnClickListner(ItemClickListner itemClickListner) {
        mItemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        mItemClickListner.onItemClick(v, getAdapterPosition(), false);
    }

}
