package com.thipna219166.onlineshoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.Prevalent.Prevalent;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText phoneEditText,passwordEditText;
    private CheckBox check_box;
    private ProgressDialog loadingbar;
    private String parentDbName="Users";

    private TextView admin_panel_link;
    private TextView no_admin_panel_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingbar = new ProgressDialog(this);
        loginButton=findViewById(R.id.login_now_btn);
        phoneEditText=findViewById(R.id.login_phone_number_input);
        passwordEditText=findViewById(R.id.login_password_input);

        check_box=findViewById(R.id.login_check_box);
        check_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_box.isChecked()){
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else{
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });

        //paper io for storage
        Paper.init(this);

        //for admin
        admin_panel_link=findViewById(R.id.admin_panel_link);
        no_admin_panel_text=findViewById(R.id.not_admin_panel_link);

        admin_panel_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText(R.string.log_in_as_admin);
                admin_panel_link.setVisibility(View.INVISIBLE);
                no_admin_panel_text.setVisibility(View.VISIBLE);
                parentDbName="Admins";
                //remember_me_check_box.setVisibility(View.GONE);
            }
        });
        no_admin_panel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.setText(R.string.login_btn);
                admin_panel_link.setVisibility(View.VISIBLE);
                no_admin_panel_text.setVisibility(View.INVISIBLE);

                parentDbName="Users";
                //remember_me_check_box.setVisibility(View.GONE);
            }
        });

        /*
        //forgot pass
        TextView forgetPassword=findViewById(R.id.forget_password_link);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
         */

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void logIn() {
        String phone = phoneEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, R.string.editview_phone_user, Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.editview_password_user, Toast.LENGTH_SHORT).show();
        }else {
            loadingbar.setTitle("login...");
            loadingbar.setMessage(getString(R.string.please_wait_text));
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();
            validateDB(phone,password);
        }

    }

    private void validateDB(final String phone, final String password) {

        final DatabaseReference rootRef;
        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(parentDbName).child(phone).exists()){
                    User users=snapshot.child(parentDbName).child(phone).getValue(User.class);
                    assert users != null;
                    if (users.getPhone().equals(phone)){
                        if (users.getPassword().equals(password)){

                            Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                            loadingbar.dismiss();
                        /*    if (remember_me_check_box.isChecked()) // if tick a check box
                            {
                                Paper.book().write(Prevalent.UserphoneKey,phone);
                                Paper.book().write(Prevalent.UserPasswordKey,password);

                            }

                         */
                            Paper.book().write(Prevalent.currentOnlineUser,users);

                            if (parentDbName.equals("Admins"))
                            {

                                Intent intent=new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            }
                            else if (parentDbName.equals("Users"))
                            {

                                //  Paper.book().write(Prevalent.currentOnlineUser,users);
                                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                            }

                        }
                        else
                        {
                            loadingbar.dismiss();
                            Toast.makeText(LoginActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        loadingbar.dismiss();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Số điện thoại "+phone+" không tồn tại", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}