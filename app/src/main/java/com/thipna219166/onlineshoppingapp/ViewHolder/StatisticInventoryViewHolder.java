package com.thipna219166.onlineshoppingapp.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thipna219166.onlineshoppingapp.Interface.ItemClickListner;
import com.thipna219166.onlineshoppingapp.R;

public class StatisticInventoryViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    public TextView nameTextView,storageTextView;
    public ItemClickListner mItemClickListner;

    public StatisticInventoryViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView=itemView.findViewById(R.id.pname_inventory_tv);
        storageTextView=itemView.findViewById(R.id.storage_inventory_tv);

    }

    public void setOnClickListner(ItemClickListner itemClickListner) {
        mItemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        mItemClickListner.onItemClick(v, getAdapterPosition(), false);
    }

}
