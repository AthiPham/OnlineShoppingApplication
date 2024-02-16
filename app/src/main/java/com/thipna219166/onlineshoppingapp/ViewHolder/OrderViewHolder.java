package com.thipna219166.onlineshoppingapp.ViewHolder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thipna219166.onlineshoppingapp.Interface.ItemClickListner;
import com.thipna219166.onlineshoppingapp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener {
    public TextView nameTextView,phoneTextView,priceTextView,addressTextView,dateTimeTextView;

    public ItemClickListner mItemClickListner;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView=itemView.findViewById(R.id.user_name_admin);
        phoneTextView=itemView.findViewById(R.id.phone_number_admin);
        priceTextView=itemView.findViewById(R.id.total_payment_admin);
        addressTextView=itemView.findViewById(R.id.address);
        dateTimeTextView=itemView.findViewById(R.id.order_date_time_admin_new);

    }

    public void setOnClickListner(ItemClickListner itemClickListner) {
        mItemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        mItemClickListner.onItemClick(v, getAdapterPosition(), false);
    }
}