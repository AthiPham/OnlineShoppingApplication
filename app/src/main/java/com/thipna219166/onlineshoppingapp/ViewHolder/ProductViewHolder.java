package com.thipna219166.onlineshoppingapp.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thipna219166.onlineshoppingapp.Interface.ItemClickListner;
import com.thipna219166.onlineshoppingapp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView productTitle,productPrice;
    public ImageView productImage;
    public ItemClickListner mItemClickListner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        productTitle = itemView.findViewById(R.id.product_name_rv);
        //productDescription = itemView.findViewById(R.id.product_description_rv);
        productImage = itemView.findViewById(R.id.product_image_rv);
        productPrice=itemView.findViewById(R.id.product_price_rv);
    }

    public void setOnItemClickListner(ItemClickListner itemClickListner) {
        this.mItemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View v) {
        mItemClickListner.onItemClick(v, getAdapterPosition(), false);
    }
}
