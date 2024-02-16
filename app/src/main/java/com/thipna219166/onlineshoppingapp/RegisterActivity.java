package com.thipna219166.onlineshoppingapp;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText phoneEditText, userNameEditText, passwordEditText;
    private ProgressDialog loadingbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button signUpButton = findViewById(R.id.sign_up);
        phoneEditText = findViewById(R.id.register_phone_number_input);
        passwordEditText = findViewById(R.id.register_password_input);
        userNameEditText = findViewById(R.id.register_user_name_input);
        loadingbar = new ProgressDialog(this);

        signUpButton.setOnClickListener(new View.OnClickListener() {
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
            Toast.makeText(this, R.string.editview_name_user, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.editview_phone_user, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.editview_password_user, Toast.LENGTH_SHORT).show();

        } else {
            loadingbar.setTitle("Creating account");
            loadingbar.setMessage("please wait..processing");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            validateDB(userName, phone, password);
        }

    }

    private void validateDB(final String userName, final String phone, final String password) {
        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if ((snapshot.child("Users").child(phone).exists())) {
                    loadingbar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Số điện thoại đã tồn tại\nHãy đăng ký với số khác", Toast.LENGTH_SHORT).show();


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

                                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));

                                        finish();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "something went wrong\n please try again", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterActivity.this, "Error: "+ error.getDetails(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}