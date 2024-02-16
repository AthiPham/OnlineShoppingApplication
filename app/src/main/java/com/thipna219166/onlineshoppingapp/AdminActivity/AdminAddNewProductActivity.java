package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;



import com.thipna219166.onlineshoppingapp.R;

public class AdminAddNewProductActivity extends AppCompatActivity {

    private String categoryName, Description, Price, Pname, saveCurrentDate, saveCurrentTime;
    private int storage;
    private Button addNewProductBtn;
    private ImageView slectImageView;
    private EditText getName, getDescription, getPrice, getStorage;
    private TextView getCategory;
    public static final int GALLERY_PICK_REQUEST_CODE = 1;
    private Uri imageUri;
    private String productRandomKey, downloadImageUrl;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);


        //
        productImageRef = FirebaseStorage.getInstance().getReference().child("products");
        productRef=FirebaseDatabase.getInstance().getReference().child("Products");


        //
        loadingbar = new ProgressDialog(this);
        //

        addNewProductBtn = findViewById(R.id.btn_add_new_product);
        slectImageView = findViewById(R.id.slect_product_image);
        getName = findViewById(R.id.product_name);
        getDescription = findViewById(R.id.product_description);
        getPrice = findViewById(R.id.product_price);
        getCategory = findViewById(R.id.product_category);
        getStorage = findViewById(R.id.product_storage);

        slectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        addNewProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateProductData();
            }
        });

        getCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayCategoryDialog();
            }
        });

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void validateProductData() {
        Description = getDescription.getText().toString();
        Pname = getName.getText().toString();
        Price = getPrice.getText().toString();
        categoryName = getCategory.getText().toString();
        String str_storage = getStorage.getText().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Ảnh sản phẩm còn trống", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Tên sản phẩm còn trống", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Mô tả sản phẩm còn trống", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Giá sản phẩm còn trống", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(categoryName)) {
            Toast.makeText(this, "Phân loại sản phẩm còn trống", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(str_storage)){
            Toast.makeText(this, "Số lượng tồn kho sản phẩm còn trống", Toast.LENGTH_SHORT).show();
        } else {
            storage = Integer.parseInt(str_storage); // can throw NumberFormatException
            storeProductInformation();
            loadingbar.setTitle("Adding new product...");
            loadingbar.setMessage(getString(R.string.please_wait_text));
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
        }
    }

    private void storeProductInformation() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy MM dd");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        productRandomKey = saveCurrentDate+saveCurrentTime;
        productRandomKey=productRandomKey.replace("."," ").replace("$"," ").replace("#"," ").replace("@"," ");


        //
        final StorageReference filePath = productImageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingbar.dismiss();
                        Toast.makeText(AdminAddNewProductActivity.this, "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw Objects.requireNonNull(task.getException());
                                }
                                downloadImageUrl = filePath.getDownloadUrl().toString();
                                return filePath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if (task.isSuccessful())
                                {
                                    downloadImageUrl=task.getResult().toString();
                                    Toast.makeText(AdminAddNewProductActivity.this, "got product image", Toast.LENGTH_SHORT).show();
                                    saveProductInfoTodatabase();
                                }
                            }
                        });
                    }
                });


    }

    private void saveProductInfoTodatabase() {
        String pid = productRef.push().getKey();

        HashMap<String,Object> productHashMap=new HashMap<>();
        productHashMap.put("pid",pid);
        productHashMap.put("date",saveCurrentDate);
        productHashMap.put("time",saveCurrentTime);
        productHashMap.put("description",Description);
        productHashMap.put("image",downloadImageUrl);
        productHashMap.put("category",categoryName);
        productHashMap.put("price",Price);
        productHashMap.put("pname",Pname);
        productHashMap.put("storage",storage);

        productRef.child(pid).updateChildren(productHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    //startActivity(new Intent(AdminAddNewProductActivity.this, AdminHomeActivity.class));
                    finish();

                }
                else {
                    loadingbar.dismiss();
                    Toast.makeText(AdminAddNewProductActivity.this, "Error:"+task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_PICK_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                slectImageView.setImageURI(imageUri);

            }
        }
    }

    private void displayCategoryDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminAddNewProductActivity.this);
        builder.setTitle("Chọn loại sản phẩm");


        // add a radio button list
        String[] categorys = {"shirt", "skirt", "coat", "dress", "pants", "bag", "hat", "shoes"};
        builder.setItems(categorys, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getCategory.setText(categorys[which]);
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}