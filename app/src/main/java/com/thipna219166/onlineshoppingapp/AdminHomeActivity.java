package com.thipna219166.onlineshoppingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Picasso;


//import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import com.thipna219166.onlineshoppingapp.AdminActivity.AdminAddNewProductActivity;
import com.thipna219166.onlineshoppingapp.AdminActivity.AdminMaintainProductActivity;
import com.thipna219166.onlineshoppingapp.AdminActivity.AdminOrderManagementActivity;
import com.thipna219166.onlineshoppingapp.AdminActivity.AdminStatisticActivity;
import com.thipna219166.onlineshoppingapp.AdminActivity.AdminUserManagementActivity;

import com.thipna219166.onlineshoppingapp.Model.Product;
import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.Prevalent.Prevalent;
import com.thipna219166.onlineshoppingapp.ViewHolder.ProductViewHolder;

public class AdminHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout drawer;

    private FloatingActionButton fab;
    private DatabaseReference mReference;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        //firebase
        mReference= FirebaseDatabase.getInstance().getReference().child("Products");



        //recyclerview
        mRecyclerView=findViewById(R.id.recycler_view_home);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);


        //drawer
        drawer = findViewById(R.id.drawer_layout);

        // Passing each menu ID as a set of Ids because each
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //Paper
        Paper.init(this);


        //fab button
        fab = findViewById(R.id.fab);
        fab.setImageResource(R.drawable.add2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminHomeActivity.this, AdminAddNewProductActivity.class));
            }
        });



            View headerview = navigationView.getHeaderView(0);
            TextView userProfileName = headerview.findViewById(R.id.user_profile_name);
            //CircleImageView userProfileimage = headerview.findViewById(R.id.user_profile_image);
            User users = Paper.book().read(Prevalent.currentOnlineUser);
            userProfileName.setText(users.getName());
            /*
            if (Paper.book().read(Prevalent.hasImageKey, false)) {
                Picasso.get().load(users.getImage()).placeholder(R.drawable.user).into(userProfileimage);
            }
             */

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(mReference,Product.class)
                .build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                if (model.getStorage()>0){ holder.productTitle.setText(model.getPname());
                } else {
                    holder.productTitle.setText(String.format("(Hết hàng) %s",model.getPname()));
                }
                //holder.productDescription.setText(model.getDescription());
                holder.productPrice.setText(String.format("Giá $%s ", model.getPrice()));
                Picasso.get().load(model.getImage()).into(holder.productImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            String id = model.getPid();
                            Intent intent = new Intent(AdminHomeActivity.this, AdminMaintainProductActivity.class);
                            intent.putExtra("pid", id);
                            startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new ProductViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.product_item,parent,false));
            }
        };
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

            if (id == R.id.nav_product_management) {

            } else if (id == R.id.nav_order_management) {
                startActivity(new Intent(AdminHomeActivity.this, AdminOrderManagementActivity.class));
            } else if (id ==  R.id.nav_user_management) {
                startActivity(new Intent(AdminHomeActivity.this, AdminUserManagementActivity.class));
            } else if (id == R.id.nav_statistic) {
                startActivity(new Intent(AdminHomeActivity.this, AdminStatisticActivity.class));
            } else if ( id == R.id.nav_logout) {
                //Paper.book().delete(Prevalent.UserPasswordKey);
                //Paper.book().delete(Prevalent.UserphoneKey);
                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

    }



    @Override
    protected void onDestroy() {
        Paper.book().delete(Prevalent.currentOnlineUser);
        super.onDestroy();
    }
}
