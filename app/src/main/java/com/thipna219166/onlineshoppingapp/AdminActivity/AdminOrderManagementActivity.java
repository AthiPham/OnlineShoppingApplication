package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.thipna219166.onlineshoppingapp.Model.Order;
import com.thipna219166.onlineshoppingapp.ViewHolder.OrderViewHolder;
import com.thipna219166.onlineshoppingapp.R;

public class AdminOrderManagementActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_management2);

        mRecyclerView=findViewById(R.id.admin_order_management_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mReference= FirebaseDatabase.getInstance().getReference().child("Orders");

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(mReference,Order.class)
                .build();
        FirebaseRecyclerAdapter<Order, OrderViewHolder> adapter = new FirebaseRecyclerAdapter<Order, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull  Order model) {
                holder.nameTextView.setText(String.format("Người nhận: %s",model.getName()));
                holder.phoneTextView.setText(String.format("Số điện thoại: %s",model.getPhone()));
                holder.addressTextView.setText(String.format("Địa chỉ nhận: %s, %s", model.getAddress(),model.getCity()));
                holder.dateTimeTextView.setText(String.format("Ngày đặt hàng: %s %s",model.getDate(), model.getTime()));
                holder.priceTextView.setText(String.format("Tổng thanh toán (VND): %s",model.getTotalPayment()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(holder.getLayoutPosition()).getKey();
                        Intent intent = new Intent(AdminOrderManagementActivity.this, AdminOrderDetailActivity.class);
                        intent.putExtra("oid", id);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new OrderViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.order_item, parent, false));
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}