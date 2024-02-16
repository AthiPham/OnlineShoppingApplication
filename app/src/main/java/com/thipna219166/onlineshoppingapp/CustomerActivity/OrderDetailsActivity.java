package com.thipna219166.onlineshoppingapp.CustomerActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.Query;

import com.thipna219166.onlineshoppingapp.Model.Cart;
import com.thipna219166.onlineshoppingapp.Model.Order;
import com.thipna219166.onlineshoppingapp.R;
import com.thipna219166.onlineshoppingapp.ViewHolder.CartViewHolder;

public class OrderDetailsActivity extends AppCompatActivity {

    private RecyclerView productsListRv;

    private TextView nameReceiverTV, phoneTV, addressTV, dateTV, totalPriceTV, stateOfOrderTV, shippingFeeTV, totalPaymentTV;

    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Cart,CartViewHolder> adapter;
    private String orderid="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        orderid = getIntent().getStringExtra("oid");

        databaseReference= FirebaseDatabase.getInstance().getReference();

        nameReceiverTV= findViewById(R.id.name_order);
        phoneTV = findViewById(R.id.phone_number_order);
        addressTV = findViewById(R.id.address_order);
        dateTV = findViewById(R.id.date_order);
        totalPriceTV = findViewById(R.id.total_price_order);
        stateOfOrderTV = findViewById(R.id.state_order);
        shippingFeeTV = findViewById(R.id.shipping_fee_order);
        totalPaymentTV = findViewById(R.id.total_payment_order);

        productsListRv=findViewById(R.id.order_details_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        productsListRv.setLayoutManager(linearLayoutManager);

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        displayOrderInfo();

    }


    private void displayOrderInfo(){
        databaseReference.child("Orders").child(orderid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Order order=snapshot.getValue(Order.class);
                    if (order!=null)
                    {
                        nameReceiverTV.setText(String.format("Người nhận: %s",order.getName()));
                        phoneTV.setText(String.format("Số điện thoại: %s",order.getPhone()));
                        addressTV.setText(String.format("Địa chỉ nhận: %s, %s",order.getAddress(),order.getCity()));
                        dateTV.setText(String.format("Ngày đặt hàng: %s, %s",order.getDate(), order.getTime()));
                        totalPriceTV.setText(String.format("Tổng đơn hàng (VNĐ): %s",order.getTotalAmount()));
                        stateOfOrderTV.setText(String.format("Tình trạng đơn: %s",order.getState()));
                        totalPaymentTV.setText(String.format("Tổng thanh toán (VND): %s",order.getTotalPayment()));
                        shippingFeeTV.setText(String.format("Phí vận chuyển (VND): %s",order.getShippingFee()));
                    }

                    else
                    {
                        Toast.makeText(OrderDetailsActivity.this, "Error (order == null)", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(OrderDetailsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();


            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        Query query = databaseReference.child("Order Products").child(orderid).orderByChild("datetime");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(query,Cart.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Cart,CartViewHolder>(options) {
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                //Log.i("Info of CartOrderRef","onBindViewHolder: display Orders" );
                holder.titleTextView.setText(model.getPname());
                holder.quantityTextView.setText(String.format("Số lượng: %s",model.getQuantity()));
                holder.priceTextView.setText(model.getPrice());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(OrderDetailsActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid", model.getPid());
                        startActivity(intent);
                    }
                });
            }
        };
        productsListRv.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
        }
    }

}