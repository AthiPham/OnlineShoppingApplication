package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;



import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;

import com.thipna219166.onlineshoppingapp.R;


public class AdminAddUserActivity extends AppCompatActivity {

    private EditText phoneEditText, userNameEditText, passwordEditText;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_user);
        Button add_user_btn = findViewById(R.id.btn_add_new_user);
        phoneEditText = findViewById(R.id.user_phone_et);
        passwordEditText = findViewById(R.id.user_password_et);
        userNameEditText = findViewById(R.id.user_name_et);
        loadingbar = new ProgressDialog(this);

        add_user_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
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

    private void createAccount() {
        String phone = phoneEditText.getText().toString();
        String userName = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Tên người dùng còn trống", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Số điện thoại còn trống", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Mật khẩu còn trống", Toast.LENGTH_SHORT).show();

        } else {
            loadingbar.setTitle("Creating account");
            loadingbar.setMessage("please wait..processing");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            ValidatUser(userName, phone, password);
        }

    }

    private void ValidatUser(final String userName, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("Users").child(phone).exists())) {
                    loadingbar.dismiss();
                    Toast.makeText(AdminAddUserActivity.this, "Số điện thoại đã tồn tại\nHãy chọn số điện thoại khác", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> userDataMap = new HashMap<>();
                    userDataMap.put("phone", phone);
                    userDataMap.put("password", password);
                    userDataMap.put("name", userName);
                    rootRef.child("Users").child(phone).updateChildren(userDataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    loadingbar.dismiss();
                                    if (task.isSuccessful()) {

                                        Toast.makeText(AdminAddUserActivity.this, "Thêm người dùng thành công", Toast.LENGTH_SHORT).show();
                                        //startActivity(new Intent(AdminAddUserActivity.this, AdminUserManagementActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(AdminAddUserActivity.this, "something went wrong\n please try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminAddUserActivity.this, "Error: "+ error.getDetails(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}