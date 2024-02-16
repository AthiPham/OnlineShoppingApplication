package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thipna219166.onlineshoppingapp.R;

import java.util.Hashtable;
import java.util.Set;

public class AdminMonthStatisticActivity extends AppCompatActivity {

    private TextView detailTextView;
    private DatabaseReference reference, productRef;
    //private String chosenMonthYear = "04 2023";

    //private String Sold ="0", Inventory = "0", Revenue = "0";
    private ProgressDialog loadingbar;

    //private ArrayList<CategoryProduct> listOfSoldCategory = new ArrayList<>();
    private Hashtable<String,String> listOfDetail = new Hashtable<String,String>();
    private Hashtable<String,String> listOfSoldNumber = new Hashtable<String, String>();
    //private List<String> mStringList = new ArrayList<String>(Arrays.asList("shirt", "skirt", "coat", "dress", "pants", "bag", "hat", "shoes"));
    private StringBuffer details;
    private long sumProducts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_month_statistic);

        String revenue = getIntent().getStringExtra("revenue");
        String monthyear = getIntent().getStringExtra("monthyear");
        //Log.i("Receive","Revenue: "+revenue+"; monthyear: "+monthyear);

        reference= FirebaseDatabase.getInstance().getReference().child("Statistic Month Year").child(monthyear);
        productRef = FirebaseDatabase.getInstance().getReference().child("Products");

        TextView strRevenueTextView = findViewById(R.id.header_text_statistic_revene_of_month);
        strRevenueTextView.setText(String.format("Doanh thu của %s (VND):",monthyear));
        TextView numRevenueTextView = findViewById(R.id.number_revenue_of_month);
        numRevenueTextView.setText(revenue);

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        detailTextView = findViewById(R.id.text_detail_sold_product);
        details = new StringBuffer("Chi tiết:");

        loadingbar = new ProgressDialog(this);

        StartStatistic();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void StartStatistic(){
        loadingbar.setTitle("Statistic detail...");
        loadingbar.setMessage(getString(R.string.please_wait_text));
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        SoldStatisticQuery();

    }

    private void SoldStatisticQuery() {

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int sumOfSoldProduct = 0;
                    sumProducts = dataSnapshot.getChildrenCount();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) { // for each pid
                        String pid = ds.getKey();
                        int soldProduct = ds.child("number of sold").getValue(Integer.class);
                        String str_numSold = String.valueOf(soldProduct);
                        sumOfSoldProduct = sumOfSoldProduct + soldProduct;
                        listOfSoldNumber.put(pid,str_numSold);

                        displayDetail(pid);
                    }
                    String str_sum = String.valueOf(sumOfSoldProduct);
                    TextView sumSoldTextView = findViewById(R.id.number_sum_sold_product);
                    sumSoldTextView.setText(str_sum);

                } else {
                    loadingbar.dismiss();
                    Log.i("dataSnapshot dont exitsts", "Error: dataSnapshot dont exitsts while statistic month.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("description", "Error: " + databaseError.getMessage());
            }
        });
    }


    private void displayDetail(String pid){
        productRef.child(pid).child("pname").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String pname = snapshot.getValue(String.class);
                int numSold = Integer.parseInt(listOfSoldNumber.get(pid));
                if (numSold >0) {
                    listOfDetail.put(pid,"\n" + pname + ": "+ listOfSoldNumber.get(pid));
                } else {
                    listOfDetail.put(pid,"");
                }
                if (listOfDetail.size()== sumProducts){
                    Set<String> keySet = listOfDetail.keySet();
                    for (String key : keySet) {
                        details.append(listOfDetail.get(key));
                    }
                    loadingbar.dismiss();
                    detailTextView.setText(details);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("description", "Display Detail Month Statistic Error: " + error.getMessage());
            }
        });
    }

}