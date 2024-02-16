package com.thipna219166.onlineshoppingapp.CustomerActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.squareup.picasso.Picasso;

import com.thipna219166.onlineshoppingapp.Model.Product;
import com.thipna219166.onlineshoppingapp.ViewHolder.ProductViewHolder;
import com.thipna219166.onlineshoppingapp.R;

public class SearchProductActivity extends AppCompatActivity {

    private EditText searchEditText;
    private RecyclerView recyclerViewSearch;
    private String searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        Button searchProductBtn = findViewById(R.id.search_btn);
        searchEditText=findViewById(R.id.search_product_name);
        recyclerViewSearch=findViewById(R.id.search_recycler_view);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewSearch.setHasFixedSize(true);

        searchProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchInput=searchEditText.getText().toString();
                onStart();
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

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Products");

        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(reference.orderByChild("pname").startAt(searchInput).endAt(searchInput+"\uf8ff"),Product.class)
                .build();
        FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter=new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull final Product model) {
                holder.productTitle.setText(model.getPname());
                //holder.productDescription.setText(model.getDescription());
                holder.productPrice.setText(String.format("Giá %s ", model.getPrice()));
                Picasso.get().load(model.getImage()).into(holder.productImage);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id=model.getPid();
                        Intent intent=new Intent(SearchProductActivity.this, ProductDetailsActivity.class);
                        intent.putExtra("pid",id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item,parent,false));
            }

        };
        recyclerViewSearch.setAdapter(adapter);
        adapter.startListening();
        // adapter.
    }

}