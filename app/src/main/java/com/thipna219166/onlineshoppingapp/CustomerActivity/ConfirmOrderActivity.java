package com.thipna219166.onlineshoppingapp.CustomerActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.thipna219166.onlineshoppingapp.HomeActivity;
import com.thipna219166.onlineshoppingapp.Model.Cart;
import com.thipna219166.onlineshoppingapp.Model.Order;
import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.Prevalent.Prevalent;
import com.thipna219166.onlineshoppingapp.R;
import com.thipna219166.onlineshoppingapp.ViewHolder.CartViewHolder;

public class ConfirmOrderActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private EditText phoneEditText,nameEditText,adressEditText,cityEditText;
    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    private RecyclerView mRecyclerView;
    private User users;

    private int totalprice, shippingFee, totalpayment;
    private String oid;
    private String saveCurrentTime,saveCurrentDate, saveCurrentMonthYear;

    private ProgressDialog loadingbar;

    private HashMap<String,Object> orderFinalHashMap;

    //private ArrayList<Cart> productsOnCart;
    //private ArrayList<Product> productDetails = new ArrayList<Product>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        reference= FirebaseDatabase.getInstance().getReference();

        phoneEditText=findViewById(R.id.phone_edit_confirm);
        nameEditText=findViewById(R.id.name_edit_confirm);
        adressEditText=findViewById(R.id.adress_edit_confirm);
        cityEditText=findViewById(R.id.city_edit_confirm);
        Button confirmButton=findViewById(R.id.confirm_button_confirm);

        Paper.init(this);
        users=Paper.book().read(Prevalent.currentOnlineUser);

        totalprice = getIntent().getIntExtra("total",0);
        TextView totalPriceTextView = findViewById(R.id.total_price_cart);
        totalPriceTextView.setText(String.format("Tổng đơn hàng (VNĐ): " + totalprice));
        cityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }


            @Override
            public void afterTextChanged(Editable s) {
                String city = cityEditText.getText().toString().trim();
                if (!city.isEmpty()){
                    shippingFee = 0;
                    if (city.equalsIgnoreCase("Hà Nội") || city.equalsIgnoreCase("Ha Noi")) {
                        shippingFee = 25000;
                    } else { shippingFee = 30000;}

                    TextView shippingFeeTextView = findViewById(R.id.shipping_fee);
                    shippingFeeTextView.setText(String.format("Phí vận chuyển (VNĐ): "+ shippingFee));

                    totalpayment = shippingFee + totalprice;
                    TextView totalPaymentTextView = findViewById(R.id.total_payment_cart);
                    totalPaymentTextView.setText(String.format("Tổng thanh toán (VNĐ): %s", totalpayment));

                    TextView totalPaymentTextView_1 = findViewById(R.id.total_payment_cart_1);
                    totalPaymentTextView_1.setText(String.format("Tổng thanh toán (VNĐ): %s", totalpayment));

                }
            }
        });

        loadingbar = new ProgressDialog(this);

        mRecyclerView=findViewById(R.id.order_details_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //Log.i("Confirm order Activity","Confirm order Activity: onCreate, before");
        //getProductsOnCart();
        //Log.i("Confirm order Activity","Confirm order Activity: onCreate, (after getProductsOnCart");

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (checkEditText())
                {
                    loadingbar.setTitle("Order...");
                    loadingbar.setMessage(getString(R.string.please_wait_text));
                    loadingbar.setCanceledOnTouchOutside(false);
                    loadingbar.show();

                    orderFinalHashMap = new HashMap<>();

                    //productsOnCart = new ArrayList<Cart>();
                    oid = reference.child("Orders").push().getKey();
                    setDateTimeOrder();

                    //Log.i("Confirm order Activity","Confirm order Activity: click button confirm Order, before");
                    getProductsOnCart();
                    //Log.i("Confirm order Activity","Confirm order Activity: click button confirm Order, after");
                }
            }
        });

    }

    private void setDateTimeOrder(){
        // set time, date, month year
        Calendar calendarForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("yyyy MM dd" );
        saveCurrentDate=currentDate.format(calendarForDate.getTime());
        saveCurrentMonthYear = saveCurrentDate.substring(0,7);

        Calendar calendarForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a" );
        saveCurrentTime=currentTime.format(calendarForTime.getTime());

    }


    private boolean checkEditText() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Tên người nhận còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(phoneEditText.getText().toString()))
        {
            Toast.makeText(this, "Số điện thoại còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(adressEditText.getText().toString()))
        {
            Toast.makeText(this, "Địa chỉ còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(cityEditText.getText().toString()))
        {
            Toast.makeText(this, "Thành phố còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }


    private void getProductsOnCart(){
        Log.i("Confirm order Activity","Confirm order Activity: loop for add product on cart");
        reference.child("Cart").child(users.getPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Cart cart = ds.getValue(Cart.class);

                        Log.i("Confirm order Activity","Confirm order Activity: loop for add link in hashmap");
                        orderFinalHashMap.put("/Order Products/"+ oid + "/" + cart.getPid(),cart);
                        orderFinalHashMap.put("/Products/"+ cart.getPid() + "/storage", ServerValue.increment(-Integer.parseInt(cart.getQuantity())));
                        orderFinalHashMap.put("/Statistic Month Year/"+ saveCurrentMonthYear+ "/" + cart.getPid() + "/number of sold",ServerValue.increment(Integer.parseInt(cart.getQuantity() )) );
                    }
                    confirmOrder();
                } else{
                    Toast.makeText(ConfirmOrderActivity.this, "Don't have information of cart of this user " , Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("description", "Error: " + error.getMessage());
            }
        });
    }

    private void confirmOrder()
    {


        Order order = new Order(adressEditText.getText().toString(),cityEditText.getText().toString()
                ,saveCurrentDate,nameEditText.getText().toString(),phoneEditText.getText().toString(),users.getPhone()
                ,"wait accept",saveCurrentTime,String.valueOf(totalprice),String.valueOf(shippingFee),String.valueOf(totalpayment));


        orderFinalHashMap.put("/Orders/" + oid,order);
        orderFinalHashMap.put("/Cart/"+ users.getPhone(),null);
        //orderFinalHashMap.put("phone",phoneEditText.getText().toString());


        reference.updateChildren(orderFinalHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingbar.dismiss();
                    //Log.i("Confirm order Activity","Confirm order Activity: success update children, after");
                    Toast.makeText(ConfirmOrderActivity.this, "Đặt đơn thành công", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    loadingbar.dismiss();
                    Toast.makeText(ConfirmOrderActivity.this, "Order failed. You can try order later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference cartRef = reference.child("Cart").child(users.getPhone());
        Query query = cartRef.orderByChild("datetime");
        FirebaseRecyclerOptions<Cart> options = new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(query,Cart.class)
                .build();
        //Log.i("Info of cartOrderRef","Info of cart orderRef: ref: Orders" + orderid );
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
        mRecyclerView.setAdapter(adapter);
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