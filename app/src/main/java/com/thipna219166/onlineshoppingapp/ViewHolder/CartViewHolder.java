package com.thipna219166.onlineshoppingapp.ViewHolder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thipna219166.onlineshoppingapp.Interface.ItemClickListner;
import com.thipna219166.onlineshoppingapp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView titleTextView, quantityTextView, priceTextView, storageTextView;

    public ItemClickListner mItemClickListner;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        titleTextView = itemView.findViewById(R.id.cart_list_title_product);
        quantityTextView = itemView.findViewById(R.id.cart_list_quantity);
        priceTextView = itemView.findViewById(R.id.cart_list_price_number);
        storageTextView = itemView.findViewById(R.id.cart_list_storage);
    }

    public void setOnClickListner(ItemClickListner itemClickListner) {
        mItemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        mItemClickListner.onItemClick(v, getAdapterPosition(), false);
    }
}