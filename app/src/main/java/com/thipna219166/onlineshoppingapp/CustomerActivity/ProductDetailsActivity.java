package com.thipna219166.onlineshoppingapp.CustomerActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import io.paperdb.Paper;

import com.thipna219166.onlineshoppingapp.Model.Product;
import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.Prevalent.Prevalent;
import com.thipna219166.onlineshoppingapp.R;

public class ProductDetailsActivity extends AppCompatActivity {

    private String pid;
    private DatabaseReference mReference;
    private ImageView productImage;
    private TextView nameTextView,descriptionTextView,priceTextView;
    private ElegantNumberButton mNumberButton;

    private String name,price,description;

    private int storage = 0;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        Intent intent=getIntent();
        pid=intent.getStringExtra("pid");
        Paper.init(this);
        currentUser= Paper.book().read(Prevalent.currentOnlineUser);


        mReference= FirebaseDatabase.getInstance().getReference().child("Products").child(pid);
        productImage=findViewById(R.id.imageView_detail);
        nameTextView=findViewById(R.id.product_name_detail);
        descriptionTextView=findViewById(R.id.product_description_detail);
        priceTextView=findViewById(R.id.product_price_detail);
        mNumberButton=findViewById(R.id.number_btn);
        Button addToCartBtn=findViewById(R.id.add_to_cart_button);

        displayProduct();

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageButton cartBtn = findViewById(R.id.cart_btn);
        cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class));
            }
        });

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( storage >= Integer.parseInt(mNumberButton.getNumber()) ) {
                    addToCartList();
                }
                else
                {
                 if (storage > 0)   {
                     Toast.makeText(ProductDetailsActivity.this, "Chỉ có thể thêm vào giỏ ít hơn/bằng "+ storage+" sản phẩm", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     Toast.makeText(ProductDetailsActivity.this,"Sản phẩm này đã hết hàng", Toast.LENGTH_SHORT).show();
                 }
                }
            }
        });

    }

    private String getDateTimeAddToCart(){
        String saveCurrentTime,saveCurrentDate;  // Sort product on cart By date add product on cart
        Calendar calendarForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("yyyy MM dd" );
        saveCurrentDate=currentDate.format(calendarForDate.getTime());

        Calendar calendarForTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a" );
        saveCurrentTime=currentTime.format(calendarForTime.getTime());
        return saveCurrentDate + " " + saveCurrentTime;
    }
    private void addToCartList() {


        final DatabaseReference cartRef=FirebaseDatabase.getInstance().getReference().child("Cart");
        final HashMap<String,Object> productHashMap=new HashMap<>();
        productHashMap.put("pid",pid);
        productHashMap.put("pname",name);
        productHashMap.put("price",price);
        productHashMap.put("quantity",mNumberButton.getNumber());
        productHashMap.put("datetime",getDateTimeAddToCart());


        cartRef.child(currentUser.getPhone()).child(pid)
                .updateChildren(productHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(ProductDetailsActivity.this, "Thêm vào giỏ thành công", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ProductDetailsActivity.this, "Something went wrong!!! try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void displayProduct() {
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product modal = snapshot.getValue(Product.class);
                if (modal!=null)
                {
                    Picasso.get().load(modal.getImage()).into(productImage);
                    name=modal.getPname();
                    price=modal.getPrice();
                    description=modal.getDescription();
                    storage = modal.getStorage();
                    if (storage>0){
                        nameTextView.setText(modal.getPname());
                    } else {
                        nameTextView.setText(String.format("(Hết hàng) %s",modal.getPname()));
                    }
                    descriptionTextView.setText(modal.getDescription());
                    priceTextView.setText( String.format("Giá: %s (VND)",modal.getPrice()) );


                }
                else
                {
                    Toast.makeText(ProductDetailsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProductDetailsActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

}