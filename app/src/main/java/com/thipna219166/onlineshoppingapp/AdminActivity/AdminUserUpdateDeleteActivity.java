package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.rey.material.widget.CheckBox;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;




import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.R;


import java.util.HashMap;

public class AdminUserUpdateDeleteActivity extends AppCompatActivity {

    private EditText nameEditText,passwordEditText;

    private TextView phoneTextView;

    private CheckBox display_password_checkbox;
    private String uid;
    private DatabaseReference mDatabaseReference;

    //private User modal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_update_delete);

        Intent intent=getIntent();
        uid=intent.getStringExtra("uid");

        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        nameEditText=findViewById(R.id.user_name_et);
        phoneTextView=findViewById(R.id.user_phone_text);
        passwordEditText=findViewById(R.id.user_password_et);
        Button updateBtn =findViewById(R.id.btn_update_user);
        Button deleteBtn =findViewById(R.id.btn_delete_user);
        display_password_checkbox = findViewById(R.id.display_password_check_box);

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        displayUserInfo();

        display_password_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (display_password_checkbox.isChecked()){
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else{
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });



        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });



        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkEditText()){
                    HashMap<String ,Object> userHashMap=new HashMap<>();
                    //   HashMap<String,Object> productHashMap=new HashMap<>();
                    userHashMap.put("uid",uid);
                    userHashMap.put("name",nameEditText.getText().toString());
                    userHashMap.put("password",passwordEditText.getText().toString());

                    mDatabaseReference.updateChildren(userHashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(AdminUserUpdateDeleteActivity.this, "Sửa người dùng thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(AdminUserUpdateDeleteActivity.this, "Something went wrong!! try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }

    private void delete() {
        mDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // should delete cart of this user
                    Toast.makeText(AdminUserUpdateDeleteActivity.this, "Xóa người dùng thành công", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Toast.makeText(AdminUserUpdateDeleteActivity.this, "Delete user failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void displayUserInfo()
    {
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                   User modal=snapshot.getValue(User.class);
                    if (modal!=null)
                    {
                        nameEditText.setText(modal.getName());
                        phoneTextView.setText(modal.getPhone());
                        passwordEditText.setText(modal.getPassword());
                    }

                else
                {
                    Toast.makeText(AdminUserUpdateDeleteActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(AdminUserUpdateDeleteActivity.this, "Database Error", Toast.LENGTH_SHORT).show();


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private boolean checkEditText() {
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Tên người dùng còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else   if (TextUtils.isEmpty(passwordEditText.getText().toString()))
        {
            Toast.makeText(this, "Mật khẩu còn trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
}