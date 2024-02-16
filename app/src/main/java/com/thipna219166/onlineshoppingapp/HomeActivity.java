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

import com.google.firebase.database.Query;
import com.thipna219166.onlineshoppingapp.CustomerActivity.CartActivity;
import com.thipna219166.onlineshoppingapp.CustomerActivity.OrderHistoryActivity;
import com.thipna219166.onlineshoppingapp.CustomerActivity.ProductDetailsActivity;
import com.thipna219166.onlineshoppingapp.CustomerActivity.SearchProductActivity;
import com.thipna219166.onlineshoppingapp.Model.Product;
import com.thipna219166.onlineshoppingapp.Model.User;
import com.thipna219166.onlineshoppingapp.Prevalent.Prevalent;
import com.thipna219166.onlineshoppingapp.ViewHolder.ProductViewHolder;

import com.squareup.picasso.Picasso;


import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    //private String typeOfUser="";
    private DrawerLayout drawer;

    private FloatingActionButton fab;
    private DatabaseReference mReference;
    private Query query;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        //firebase
        mReference= FirebaseDatabase.getInstance().getReference().child("Products");
        query = mReference.orderByChild("storage").startAt(1);

/*
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
       if (bundle!=null)
        {
            typeOfUser=bundle.getString("Admin");

        }  */

        //recyclerview
        mRecyclerView=findViewById(R.id.recycler_view_home);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, CartActivity.class));
            }
        });

       /* if (typeOfUser.equals("Admin")){
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            navigationView.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        } */

        //header view

       // if (!typeOfUser.equals("Admin"))
       // {
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

       // }

    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions.Builder<Product>().
                setQuery(query,Product.class)
                .build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                holder.productTitle.setText(model.getPname());
                //holder.productDescription.setText(model.getDescription());
                holder.productPrice.setText(String.format("Giá (VND) %s ", model.getPrice()));
                Picasso.get().load(model.getImage()).into(holder.productImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            String id = model.getPid();
                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
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
       /* if (typeOfUser.equals("Admin")){
            super.onBackPressed();
        }
        else { */

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
      //  }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
     //   if (!typeOfUser.equals("Admin")) {
            if (id == R.id.nav_cart) {
                startActivity(new Intent(HomeActivity.this, OrderHistoryActivity.class));
            } else if (id ==  R.id.nav_search) {
                startActivity(new Intent(HomeActivity.this, SearchProductActivity.class));
            //} else if (id == R.id.nav_setting) {
                //startActivity(new Intent(HomeActivity.this, SettingActivity.class));
            } else if ( id == R.id.nav_logout) {
                //Paper.book().delete(Prevalent.UserPasswordKey);
                //Paper.book().delete(Prevalent.UserphoneKey);
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
   //     }
    //    return false;
    }



    @Override
    protected void onDestroy() {
        Paper.book().delete(Prevalent.currentOnlineUser);
        super.onDestroy();
    }
}