package com.thipna219166.onlineshoppingapp.AdminActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;


import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.thipna219166.onlineshoppingapp.Model.Product;
import com.thipna219166.onlineshoppingapp.R;
import com.thipna219166.onlineshoppingapp.ViewHolder.StatisticInventoryViewHolder;

public class AdminStatisticActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener, OnChartValueSelectedListener, OnChartGestureListener {

    private FirebaseRecyclerAdapter<Product, StatisticInventoryViewHolder> adapter;
    private DatabaseReference reference, storageRef;
    private String chosenYear;

    private ProgressDialog loadingbar;
    //private ArrayList<CategoryProduct> listOfSoldCategory = new ArrayList<>();
    private Hashtable<String,String> listOfRevenueMonths = new Hashtable<>();
    private List<String> mStringList;
    private View frame_detail_inventory;
    //private View frame1,frame2;

    private BarChart mBarChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_statistic);

        reference= FirebaseDatabase.getInstance().getReference();
        storageRef = reference.child("Products");

        loadingbar = new ProgressDialog(this);


        frame_detail_inventory = findViewById(R.id.frame_statistic_inventory_detail);
        frame_detail_inventory.setVisibility(View.INVISIBLE);

        InventoryStatisticQuery();

        TextView detailInventory = findViewById(R.id.text_detail_inventory);
        detailInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (frame_detail_inventory.getVisibility()==View.VISIBLE){
                    frame_detail_inventory.setVisibility(View.GONE);
                    if(adapter != null) {
                        adapter.stopListening();
                    }
                }
                else {
                    GetRecyclerview();
                    frame_detail_inventory.setVisibility(View.VISIBLE);

                }
            }
        });


        Button btnStatistic = findViewById(R.id.btn_statistic);
        btnStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog();
            }
        });

        mBarChart = findViewById(R.id.barchart_revenue);
        mBarChart.setVisibility(View.GONE);

        ImageButton backIBtn = findViewById(R.id.back_btn);
        backIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void InventoryStatisticQuery(){

        storageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int sumAvailable = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {  // tung sp trong category
                        int storage = ds.child("storage").getValue(Integer.class);
                        sumAvailable = sumAvailable + storage;
                    }
                    TextView sumInventoryTextView = findViewById(R.id.number_sum_inventory);
                    sumInventoryTextView.setText(String.valueOf(sumAvailable));

                } else {
                    Log.i("dataSnapshot dont exitsts", "Error: dataSnapshot dont exitsts product reference.");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("description", "Error (Storage Statistic Query): " + databaseError.getMessage());
            }
        });

    }

    private void GetRecyclerview(){
        RecyclerView InventoryRecyclerView = findViewById(R.id.admin_statistic_inventory_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        InventoryRecyclerView.setLayoutManager(linearLayoutManager);

        Query query = storageRef.orderByChild("storage");
        FirebaseRecyclerOptions<Product> options=new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class).build();
        adapter=new FirebaseRecyclerAdapter<Product, StatisticInventoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull StatisticInventoryViewHolder holder, int position, @NonNull final Product model) {
                holder.nameTextView.setText( String.format("Tên sản phẩm: %s",model.getPname()) );
                holder.storageTextView.setText( String.format("Tồn kho: %s",model.getStorage()) );
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = model.getPid();
                        Intent intent = new Intent(AdminStatisticActivity.this, AdminMaintainProductActivity.class);
                        intent.putExtra("pid", id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public StatisticInventoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new StatisticInventoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_inventory_item,parent,false));
            }

        };
        InventoryRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    private int getCurrentYear(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    private int getCurrentMonth(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) +1;
    }
    private void getListOfMonth(int chosen_year, int current_year, int current_month){
        if (chosen_year < current_year) {
            mStringList = new ArrayList<>(Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"));
        } else {
            mStringList = new ArrayList<>();
            for (int i = 1; i <= current_month; ++i){
                    if (i<=9){
                        mStringList.add("0"+ i);}
                    else {
                        mStringList.add(String.valueOf(i));
                    }
            }
        }
    }
    private void showNumberPickerDialog(){
        final Dialog dlg = new Dialog(AdminStatisticActivity.this);
        dlg.setTitle("Chọn năm để thống kê");
        dlg.setContentView(R.layout.number_picker_dialog);
        Button ok_btn = dlg.findViewById(R.id.ok_button);
        Button cancel_btn = dlg.findViewById(R.id.cancel_button);
        final NumberPicker numberPicker =  dlg.findViewById(R.id.numberPicker1);
        numberPicker.setMaxValue(getCurrentYear());
        numberPicker.setMinValue(2023);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(this);
        ok_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                chosenYear = String.valueOf(numberPicker.getValue());
                TextView strSumRevenueTextView = findViewById(R.id.text_sum_revenue);
                strSumRevenueTextView.setText(String.format("Tổng doanh thu năm %s (VND): ",chosenYear));
                dlg.dismiss();
                StartRevenueStatistic();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        dlg.show();

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        Log.i("value is", "" + newVal);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void StartRevenueStatistic(){
        loadingbar.setTitle("Statistic...");
        loadingbar.setMessage(getString(R.string.please_wait_text));
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();
        getListOfMonth( Integer.parseInt(chosenYear),getCurrentYear(), getCurrentMonth());
        RevenueStatisticQuery(chosenYear);

    }

    private void RevenueStatisticQuery(String year) {
        for (String month: mStringList) {
            Query query = reference.child("Orders").orderByChild("date").startAt(year + " " + month + " ").endAt(year + " " + month + " \uf8ff");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        int Revenue = 0;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) { // tung order trong month
                            int storage = Integer.parseInt(Objects.requireNonNull(ds.child("totalAmount").getValue(String.class)));
                            Revenue = Revenue + storage;
                        }
                        String str_revenue = String.valueOf(Revenue);
                        listOfRevenueMonths.put(month, str_revenue);

                    } else {
                        Log.i("dataSnapshot dont exitsts", "Error: dataSnapshot dont exitsts order with order month year." + month + "/" + chosenYear);
                        listOfRevenueMonths.put(month, "0");
                    }

                    if (listOfRevenueMonths.size() == mStringList.size()) {
                        DisplayRevenueStatistic();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("description", "Error (Storage Statistic Query): " + databaseError.getMessage());
                }
            });
        }
    }




    private void DisplayRevenueStatistic() {
        if (listOfRevenueMonths.size() > 0) {
            StringBuilder str_detail_revenue = new StringBuilder("Chi tiết:");

            /*
            for (int i = 0; i < listOfRevenueMonths.size() - 1; i++) {
                str_detail_revenue.append(listOfSoldCategory.get(i).getCpname() + ": " + listOfSoldCategory.get(i).getSumOfMoney() + "\n");
            }
             */
            int sumRevenue = 0;
            for (String key: mStringList){
                str_detail_revenue.append("\nTháng ").append(key).append(": ").append(listOfRevenueMonths.get(key));
                sumRevenue = sumRevenue + Integer.parseInt(Objects.requireNonNull(listOfRevenueMonths.get(key)));
            }


            TextView detail_Revenue = findViewById(R.id.text_detail_revenue);
            detail_Revenue.setText(str_detail_revenue);

            TextView tv_sum_revenue = findViewById(R.id.number_sum_revenue);
            tv_sum_revenue.setText(String.valueOf(sumRevenue));

            showRevenueMonthBarChart();
        } else {
            Log.i("Display Revenue Statistic","Revenue Statistic Error: size of list of Months =0");
        }

        loadingbar.dismiss();

    }


    private void showRevenueMonthBarChart(){



        //prepare Bar Entries
        ArrayList<BarEntry> entries = new ArrayList<>();
        int i =0;
        for (String key : mStringList) {
            BarEntry barEntry = new BarEntry(++i, Integer.parseInt(Objects.requireNonNull(listOfRevenueMonths.get(key))) ); //start always from x=1 for the first bar
            entries.add(barEntry);
        }


        //initialize x Axis Labels (labels for 13 vertical grid lines)
        final ArrayList<String> xAxisLabel = new ArrayList<>(mStringList);
        xAxisLabel.add(""); //empty label for the last vertical grid line on Y-Right Axis



        //initialize xAxis
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(14);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisMinimum(0 + 0.5f); //to center the bars inside the vertical grid lines we need + 0.5 step
        xAxis.setAxisMaximum(entries.size() + 0.5f); //to center the bars inside the vertical grid lines we need + 0.5 step
        xAxis.setLabelCount(xAxisLabel.size(), true); //draw x labels for 13 vertical grid lines
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setXOffset(0f); //labels x offset in dps
        xAxis.setYOffset(0f); //labels y offset in dps
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return xAxisLabel.get((int) value);
            }
        });


        //set the BarDataSet
        BarDataSet barDataSet = new BarDataSet(entries, "Revenue of year "+ chosenYear);
        barDataSet.setColor(R.color.colorPrimary);
        barDataSet.setFormSize(15f);
        barDataSet.setDrawValues(true); // display value of column
        barDataSet.setValueTextSize(12f);

        //set the BarData to chart
        BarData data = new BarData(barDataSet);
        mBarChart.setData(data);
        mBarChart.setScaleEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(true);
        //mBarChart.setTouchEnabled(true);
        mBarChart.setOnChartValueSelectedListener(this);
        mBarChart.setOnChartGestureListener(this);
        mBarChart.setHighlightPerDragEnabled(false);
        mBarChart.invalidate();
        mBarChart.setVisibility(View.VISIBLE);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (SelectedYIndex != null && h!= null && h.getYPx() <= SelectedYIndex) {
            Log.i("Bar Chart","if true on click");
            int revenueOfMonth = (int)e.getY();
            String revenue = String.valueOf(revenueOfMonth);
            int index = (int) e.getX() - 1;
            String monthyear = chosenYear + " " + mStringList.get(index);

            Intent intent = new Intent(AdminStatisticActivity.this, AdminMonthStatisticActivity.class);
            intent.putExtra("monthyear", monthyear);
            intent.putExtra("revenue", revenue);
            startActivity(intent);
        } else {
            mBarChart.highlightValues(null);
        }
    }

    @Override
    public void onNothingSelected() {
        mBarChart.highlightValues(null);
    }


    private Float SelectedYIndex;
    @Override
    public void onChartSingleTapped(android.view.MotionEvent me){
       Entry entry = mBarChart.getEntryByTouchPoint(me.getX(),me.getY());
       if (entry != null){
           SelectedYIndex = me.getY();
           Log.i("Bar Chart","Bar chart: motionEvent != null");
       } else {
           SelectedYIndex = null;
           Log.i("Bar Chart","Bar chart: motionEvent == null");
       }
    }


    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }


    @Override
    public void onChartGestureStart(android.view.MotionEvent me,
                                         ChartTouchListener.ChartGesture lastPerformedGesture){

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }


}