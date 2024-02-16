package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import com.squareup.picasso.Picasso;

import com.thipna219166.onlineshoppingapp.Model.Product;
import com.thipna219166.onlineshoppingapp.R;

public class AdminMaintainProductActivity extends AppCompatActivity {
    private EditText nameEditText,descriptionEditText,priceEditText, storagrEditText;
    private TextView categoryEditText;
    private ImageView productImageView;
    private String id;
    private DatabaseReference mDatabaseReference;
    private Product modal;
    private StorageReference imageref;
    private String imageUrl;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);

        id=getIntent().getStringExtra("pid");
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Products").child(id);

        productImageView = findViewById(R.id.imageView_detail_maintain);
        nameEditText=findViewById(R.id.product_name_detail_maintain);
        descriptionEditText=findViewById(R.id.product_description_detail_maintain);
        priceEditText=findViewById(R.id.product_price_detail_maintain);
        storagrEditText=findViewById(R.id.product_storage_detail_maintain);
        categoryEditText = findViewById(R.id.product_category_detail_maintain);
        Button applyButton=findViewById(R.id.apply_btn_maintain);
        Button deleteBtn=findViewById(R.id.delete_product);
        imageref= FirebaseStorage.getInstance().getReference();

        loadingbar = new ProgressDialog(this);
        displayProductInfo();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWaiting("Delete Product");
                delete();
            }
        });

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        categoryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCategoryDialog();
            }
        });

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEditText()){
                    startWaiting("Update product");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("yyyy MM dd");
                    String saveCurrentDate = currentDate.format(calendar.getTime());
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
                    String saveCurrentTime = currentTime.format(calendar.getTime());

                    int storage = Integer.parseInt(storagrEditText.getText().toString()); // throw NumberFormatException

                    HashMap<String ,Object> productHashMap=new HashMap<>();
                    //   HashMap<String,Object> productHashMap=new HashMap<>();
                    productHashMap.put("pid",id);
                    productHashMap.put("date",saveCurrentDate);
                    productHashMap.put("time",saveCurrentTime);
                    productHashMap.put("description",descriptionEditText.getText().toString());
                    productHashMap.put("image",modal.getImage());
                    productHashMap.put("category",modal.getCategory());
                    productHashMap.put("price",priceEditText.getText().toString());
                    productHashMap.put("storage",storage);
                    productHashMap.put("pname",nameEditText.getText().toString());

                    mDatabaseReference.updateChildren(productHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        finishWaiting();
                                        Toast.makeText(AdminMaintainProductActivity.this, "Sửa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else
                                    {
                                        finishWaiting();
                                        Toast.makeText(AdminMaintainProductActivity.this, "Something went wrong!! try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    private void startWaiting(String title){
        loadingbar.setTitle(title);
        loadingbar.setMessage("please wait..processing");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
    }
    private void finishWaiting(){
        loadingbar.dismiss();
    }

    private void delete() {
        mDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    imageref.child(imageUrl).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                //Toast.makeText(AdminMaintainProductActivity.this, "Image deleted succesfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    finishWaiting();
                    Toast.makeText(AdminMaintainProductActivity.this, "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    finishWaiting();
                    Toast.makeText(AdminMaintainProductActivity.this, "Error occored", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void displayProductInfo()
    {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    modal=snapshot.getValue(Product.class);
                    if (modal!=null)
                    {
                        nameEditText.setText(modal.getPname());
                        descriptionEditText.setText(modal.getDescription());
                        priceEditText.setText(modal.getPrice());
                        imageUrl=modal.getImage();
                        categoryEditText.setText(modal.getCategory());
                        storagrEditText.setText(String.valueOf(modal.getStorage()));
                        Picasso.get().load(modal.getImage()).into(productImageView);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private boolean checkEditText() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Tên sản phẩm còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(descriptionEditText.getText().toString()))
        {
            Toast.makeText(this, "Mô tả sản phẩm còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(priceEditText.getText().toString()))
        {
            Toast.makeText(this, "Giá sản phẩm còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(categoryEditText.getText().toString())){
            Toast.makeText(this, "Phân loại sản phẩm còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (TextUtils.isEmpty(storagrEditText.getText().toString())){
            Toast.makeText(this, "Tồn kho sản phẩm còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    private void displayCategoryDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminMaintainProductActivity.this);
        builder.setTitle("Choose category");


        // add a radio button list
        String[] categorys = {"shirt", "skirt", "coat", "dress", "pants", "bag", "hat", "shoes"};
        builder.setItems(categorys, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryEditText.setText(categorys[which]);
                modal.setCategory(categorys[which]);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

}