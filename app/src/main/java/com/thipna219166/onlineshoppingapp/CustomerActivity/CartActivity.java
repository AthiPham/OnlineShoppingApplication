package com.thipna219166.onlineshoppingapp.CustomerActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

import com.thipna219166.onlineshoppingapp.Model.Cart;
import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.Prevalent.Prevalent;
import com.thipna219166.onlineshoppingapp.ViewHolder.CartViewHolder;
import com.thipna219166.onlineshoppingapp.R;


import java.util.Hashtable;
import java.util.Set;

public class CartActivity extends AppCompatActivity {

    private TextView totalPriceTextView;
    private Button nextButton;
    private DatabaseReference databaseReference;
    private String uid;

    private FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter;
    private int totalPrice=0;
    private long totalProducts=0;


    private Hashtable<String,Cart> productsOnCart = new Hashtable<String,Cart>();
    private Hashtable<String,Boolean> availableQuantity = new Hashtable<String,Boolean>();
    private int IsQuantityMoreThanStorage = -1; // 0 false, 1 true, -1 not finished check


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        totalPriceTextView=findViewById(R.id.total_price_cart);
        nextButton=findViewById(R.id.next_cartList);
        //nextButton.setVisibility(View.INVISIBLE);
        RecyclerView mRecyclerView=findViewById(R.id.order_details_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        Paper.init(this);
        User curentUser =Paper.book().read(Prevalent.currentOnlineUser);
        uid = curentUser.getPhone();

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IsQuantityMoreThanStorage== 1 ) {
                    Toast.makeText(CartActivity.this, "Số lượng trong giỏ nhiều hơn lượng tồn kho", Toast.LENGTH_SHORT).show();
                }
                else if (IsQuantityMoreThanStorage == 0)
                {
                    Intent intent = new Intent(CartActivity.this, ConfirmOrderActivity.class);
                    intent.putExtra("total",totalPrice);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(CartActivity.this, "Please click button later", Toast.LENGTH_SHORT).show();
                }

            }
        });


        DatabaseReference cartRef=databaseReference.child("Cart").child(uid);
        Query query = cartRef.orderByChild("datetime");

        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>().setQuery(query,Cart.class).build();

        adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {


            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model)
            {
                int quantity=Integer.parseInt(model.getQuantity());

                String key = this.getRef(position).getKey();
                DatabaseReference productRef = databaseReference.child("Products");
                productRef.child(key).child("storage").addValueEventListener(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int storage = dataSnapshot.getValue(Integer.class);

                        if (storage < Integer.parseInt(model.getQuantity())){
                            //IsQuantityMoreThanStorage =1;
                            if (storage ==0){
                                holder.titleTextView.setText(String.format("(Hết hàng) %s",model.getPname()));
                                holder.storageTextView.setText("");
                               //Log.i("Cart Activity","Cart Activity: display Products detail ( out of stock:)"+ model.getPname());
                            }
                            else {
                            String str_storage = String.valueOf(storage);
                            holder.storageTextView.setText(String.format("Tồn kho: %s",str_storage));
                            //Log.i("Cart Activity","Cart Activity: find Products detail (storage and category)"+ model.getPname()+" " + str_storage);
                            }
                        }
                        else {
                            holder.titleTextView.setText(model.getPname());
                            holder.storageTextView.setText("");
                        }
                    }

                    public void onCancelled(@NonNull DatabaseError firebaseError) {
                        Log.e("Error Firebase","Error cannot get storage of Product:" + firebaseError.getDetails());
                    }
                });


                 holder.titleTextView.setText(model.getPname());
                 //Log.i("Cart Activity","Cart Activity: display Products detail" + model.getPname());
                holder.quantityTextView.setText(String.format("Số lượng: %s",model.getQuantity()));
                holder.priceTextView.setText(model.getPrice());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CharSequence option[]=new CharSequence[]{
                                "Sửa"
                                ,"Xóa"
                        };
                        final AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Lựa chọn:");
                        builder.setItems(option, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which==0)
                                {
                                    Intent intent=new Intent(CartActivity.this, ProductDetailsActivity.class);
                                    intent.putExtra("pid",model.getPid());
                                    startActivity(intent);

                                }
                                else if (which==1)
                                {
                                    cartRef.child(model.getPid()).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        Toast.makeText(CartActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(CartActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });
                                }
                            }
                        });
                        builder.setCancelable(true);
                        builder.show();
                    }
                });


            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CartViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.cart_item,parent,false));
            }
        };
        mRecyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        totalPrice = 0;
        nextButton.setVisibility(View.INVISIBLE);
        checkStorageAndTotalPrice();
        adapter.notifyDataSetChanged();
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


   private void checkStorageAndTotalPrice(){
        databaseReference.child("Cart").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    totalPrice = 0; // calculate when update/delete product on cart
                    nextButton.setVisibility(View.INVISIBLE);
                    productsOnCart.clear();
                    availableQuantity.clear();
                    totalProducts = snapshot.getChildrenCount();
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Cart product = ds.getValue(Cart.class);
                        int quantity = Integer.parseInt(product.getQuantity());
                        int price = Integer.parseInt(product.getPrice());
                        productsOnCart.put(product.getPid(),product);
                        totalPrice = totalPrice + ( quantity * price);
                        databaseReference.child("Products").child(product.getPid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    int storage = snapshot.child("storage").getValue(Integer.class);
                                    String pid = snapshot.getKey();
                                    if (storage >= Integer.parseInt(productsOnCart.get(pid).getQuantity())){
                                        availableQuantity.put(pid,true);
                                    } else {
                                        availableQuantity.put(pid,false);
                                    }

                                    if (availableQuantity.size() == totalProducts){
                                        Set<String> keySet = availableQuantity.keySet();
                                        IsQuantityMoreThanStorage = -1;
                                        for (String key : keySet) {
                                            if (Boolean.FALSE.equals(availableQuantity.get(key))){
                                                IsQuantityMoreThanStorage = 1;
                                                break;
                                            }
                                        }
                                        if (IsQuantityMoreThanStorage == -1) {IsQuantityMoreThanStorage = 0;}
                                        nextButton.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Error Firebase","Error cannot get storage of Product:" + error.getDetails());
                            }
                        });
                    }

                    totalPriceTextView.setText(String.format("Tổng tiền: %s VND",totalPrice));
                }
                else {
                    nextButton.setVisibility(View.INVISIBLE);
                    totalPriceTextView.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Error Firebase","Error cannot get products on Cart:" + error.getDetails());
            }
        });
   }

}