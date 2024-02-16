package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.thipna219166.onlineshoppingapp.Model.Cart;
import com.thipna219166.onlineshoppingapp.Model.Order;
import com.thipna219166.onlineshoppingapp.R;
import com.thipna219166.onlineshoppingapp.ViewHolder.CartViewHolder;

import java.util.Hashtable;

public class AdminOrderDetailActivity extends AppCompatActivity {

    private RecyclerView productsListRv;
    private Button cancel_order_btn, next_step_btn;
    private TextView nameReceiverTV, phoneTV, addressTV, dateTV, totalPriceTV, stateOfOrderTV, totalPaymentTV, shippingFeeTV;

    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Cart,CartViewHolder> adapter;

    private String orderid="";
    private Hashtable<String,Object> cancelInfo;
    //private String uidOfOrder;
    private String orderMonthYear;
    private String stateOfOrder;
    private ProgressDialog loadingbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_detail);


        orderid = getIntent().getStringExtra("oid");

        databaseReference= FirebaseDatabase.getInstance().getReference();

        nameReceiverTV= findViewById(R.id.admin_name_order);
        phoneTV = findViewById(R.id.admin_phone_number_order);
        addressTV = findViewById(R.id.admin_address_order);
        dateTV = findViewById(R.id.admin_date_order);
        totalPriceTV = findViewById(R.id.admin_total_price_order);
        stateOfOrderTV = findViewById(R.id.admin_state_order);
        shippingFeeTV =findViewById(R.id.admin_shipping_fee_order);
        totalPaymentTV = findViewById(R.id.admin_total_payment_order);

        productsListRv=findViewById(R.id.order_details_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        productsListRv.setLayoutManager(linearLayoutManager);

        cancel_order_btn = findViewById(R.id.cancel_order_btn);
        next_step_btn = findViewById(R.id.next_step_order_btn);

        loadingbar = new ProgressDialog(this);

        displayOrderInfo();



        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        // cancel button
        cancel_order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingbar.setTitle("Cancel...");
                loadingbar.setMessage(getString(R.string.please_wait_text));
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();

                cancelInfo = new Hashtable<>();
                cancelOrder();
            }
        });

        // change state of order button
        next_step_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nextState;
                if (stateOfOrder.equals("wait accept")){
                    nextState ="unpacked";
                }else if (stateOfOrder.equals("unpacked")) {
                    nextState = "shipping";
                }else if (stateOfOrder.equals("shipping")) {
                    nextState = "received";
                } else {nextState = null;}
                //Log.i("Order detail", "change state of order: "+ nextState);
                changeStateOfOrder(nextState);
            }
        });


    }

    private void displayOrderInfo(){
        loadingbar.setTitle("Loading Order Information...");
        loadingbar.setMessage(getString(R.string.please_wait_text));
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        databaseReference.child("Orders").child(orderid).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Order order=snapshot.getValue(Order.class);
                    if (order!=null)
                    {
                        nameReceiverTV.setText(String.format("Người nhận: %s",order.getName()));
                        phoneTV.setText(String.format("Số điện thoại: %s",order.getPhone()));
                        addressTV.setText(String.format("Địa chỉ nhận: %s, %s",order.getAddress(),order.getCity()));
                        dateTV.setText(String.format("Ngày đặt hàng: %s %s",order.getDate(),order.getTime()));
                        totalPriceTV.setText(String.format("Tổng đơn hàng (VND): %s",order.getTotalAmount()));
                        totalPaymentTV.setText(String.format("Tổng thanh toán (VND): %s",order.getTotalPayment()));
                        shippingFeeTV.setText(String.format("Phí vận chuyển (VND): %s",order.getShippingFee()));
                        stateOfOrder = order.getState();
                        //stateOfOrderTV.setText(String.format("Tình trạng đơn: %s",order.getState()));

                        if (stateOfOrder.equals("wait accept")) {
                            stateOfOrderTV.setText("Tình trạng đơn: Chờ xác nhận");
                            next_step_btn.setText(R.string.btn_next_step_accept);
                        }else if (stateOfOrder.equals("unpacked")) {
                            stateOfOrderTV.setText("Tình trạng đơn: Chờ lấy hàng");
                            next_step_btn.setText(R.string.btn_next_step_packed);
                        }else if (stateOfOrder.equals("shipping")) {
                            stateOfOrderTV.setText("Tình trạng đơn: Đang giao");
                            next_step_btn.setText(R.string.btn_next_step_received);
                        }else if (stateOfOrder.equals("received")) {
                            stateOfOrderTV.setText("Tình trạng đơn: Đã giao");
                            next_step_btn.setVisibility(View.INVISIBLE);
                            cancel_order_btn.setVisibility(View.INVISIBLE);
                        }else if(stateOfOrder.equals("canceled")) {
                            stateOfOrderTV.setText("Tình trạng đơn: Đã hủy");
                            next_step_btn.setVisibility(View.INVISIBLE);
                            cancel_order_btn.setVisibility(View.INVISIBLE);
                        } else {
                            next_step_btn.setVisibility(View.INVISIBLE);
                            cancel_order_btn.setVisibility(View.INVISIBLE);
                        }
                        loadingbar.dismiss();
                        //uidOfOrder = order.getUid();
                        orderMonthYear = order.getDate().substring(0,7);

                    }

                    else
                    {
                        loadingbar.dismiss();
                        Toast.makeText(AdminOrderDetailActivity.this, "Error (order == null)", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AdminOrderDetailActivity.this, "Database Error", Toast.LENGTH_SHORT).show();


            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();
        // recycle view
        Query query = databaseReference.child("Order Products").child(orderid).orderByChild("datetime");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(query,Cart.class)
                .build();
        //Log.i("Info of CartOrderRef","Info of cart orderRef: ref: Orders" + orderid );
        adapter = new FirebaseRecyclerAdapter<Cart,CartViewHolder>(options) {
            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false));
            }

            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                Log.i("Info of CartOrderRef","onBindViewHolder: display Orders" );
                holder.titleTextView.setText(model.getPname());
                holder.quantityTextView.setText(String.format("Số lượng: %s",model.getQuantity()));
                holder.priceTextView.setText(model.getPrice());
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

    private void changeStateOfOrder(String newState){
        if (newState!=null){
            databaseReference.child("Orders").child(orderid).child("state")
                    .setValue(newState).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                  @Override
                                                                  public void onComplete(@NonNull Task<Void> task) {
                                                                      if (task.isSuccessful())
                                                                      {
                                                                          Toast.makeText(AdminOrderDetailActivity.this, "Cập nhật đơn hàng thành công", Toast.LENGTH_SHORT).show();
                                                                          //finish(); //finish and return Orders History
                                                                      }
                                                                      else
                                                                      {
                                                                          Toast.makeText(AdminOrderDetailActivity.this, "Something went wrong!! try again", Toast.LENGTH_SHORT).show();
                                                                      }
                                                                  }
                     });
        }
    }


    private void cancelOrder(){
        databaseReference.child("Order Products").child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Cart cart = ds.getValue(Cart.class);

                        //Log.i("Cancel order Activity","Cancel order Activity: loop for add link in hashtable");
                        //cancelInfo.put("/Cart/"+ uidOfOrder + "/" + cart.getPid(),cart); //not need to add product to cart
                        cancelInfo.put("/Products/"+ cart.getPid() + "/storage", ServerValue.increment(Integer.parseInt(cart.getQuantity())));
                        cancelInfo.put("/Statistic Month Year/"+ orderMonthYear+ "/" + cart.getPid() + "/number of sold",ServerValue.increment(-Integer.parseInt(cart.getQuantity() )) );
                    }

                    cancelInfo.put("/Orders/"+orderid+"/state","canceled");
                    databaseReference.updateChildren(cancelInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                loadingbar.dismiss();
                                Toast.makeText(AdminOrderDetailActivity.this, "Hủy đơn thành công", Toast.LENGTH_SHORT).show();
                                //finish(); //finish and return Orders History
                            } else {
                                loadingbar.dismiss();
                                Toast.makeText(AdminOrderDetailActivity.this, "Cancel order failed. You can try cancel later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                } else{
                    loadingbar.dismiss();
                    Toast.makeText(AdminOrderDetailActivity.this, "Don't have information of order products. Cancel order failed. " , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("description", "Error: " + error.getMessage());
            }
        });
    }

}