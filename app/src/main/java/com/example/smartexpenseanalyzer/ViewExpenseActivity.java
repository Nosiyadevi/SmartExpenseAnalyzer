package com.example.smartexpenseanalyzer;
import android.view.View;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewExpenseActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView txtTotal, tvMonth;
    Spinner spinnerMonth;
    ProgressBar progressBar;
    PieChart pieChart;

    DBHelper db;
    ExpenseAdapter adapter;
    ArrayList<Expense> expenseList;

    BarChart barChart;

    Button btnToggleChart;

    boolean showingBar = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        recyclerView = findViewById(R.id.recyclerView);
        txtTotal = findViewById(R.id.txtTotal);
        tvMonth = findViewById(R.id.tvMonth);
        spinnerMonth = findViewById(R.id.spinnerMonth);
        progressBar = findViewById(R.id.progressBar);
        btnToggleChart = findViewById(R.id.btnToggleChart);
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        btnToggleChart.setOnClickListener(v -> {
            if (!showingBar) {
                pieChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                btnToggleChart.setText("Show Pie Chart");
                showingBar = true;
            } else {
                barChart.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                btnToggleChart.setText("Show Bar Chart");
                showingBar = false;
            }
        });

        db = new DBHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        expenseList = new ArrayList<>();
        adapter = new ExpenseAdapter(this, expenseList);
        recyclerView.setAdapter(adapter);

        setupMonthSpinner();
    }

    private void setupMonthSpinner() {
        ArrayList<String> months = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        String[] names = new DateFormatSymbols().getMonths();

        for (int i = 0; i < 12; i++) {
            months.add(names[i] + " " + cal.get(Calendar.YEAR));
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, months);

        spinnerMonth.setAdapter(adapter);
        spinnerMonth.setSelection(cal.get(Calendar.MONTH));

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int pos, long id) {
                loadExpenses(months.get(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void loadExpenses(String month) {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null) return;

        expenseList.clear();
        expenseList.addAll(db.getExpensesByMonth(month, uid));
        adapter.notifyDataSetChanged();

        double total = db.getTotalSpent(month, uid);
        txtTotal.setText("₹" + total);

        double budget = db.getBudget(uid);
        if (budget > 0) {
            int percent = (int) ((total / budget) * 100);
            progressBar.setProgress(Math.min(percent, 100));
        } else {
            progressBar.setProgress(0);
        }

        setupPieChart(expenseList);
        loadMonthlyBarChart();

    }
    private void setupPieChart(ArrayList<Expense> list) {

        pieChart.clear();

        if (list.isEmpty()) {
            pieChart.setNoDataText("No chart data available");
            pieChart.invalidate();
            return;
        }

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (Expense e : list) {
            entries.add(new PieEntry((float) e.amount, e.category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(
                Color.parseColor("#4CAF50"),
                Color.parseColor("#2196F3"),
                Color.parseColor("#FFC107"),
                Color.parseColor("#F44336"),
                Color.parseColor("#9C27B0")
        );

        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));

        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setCenterText("Total\n₹" + getTotalAmount(list));
        pieChart.setCenterTextSize(16f);
        pieChart.getDescription().setEnabled(false);

        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();
    }
    private float getTotalAmount(ArrayList<Expense> list) {
        float sum = 0;
        for (Expense e : list) sum += e.amount;
        return sum;
    }

    private void loadMonthlyBarChart() {

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        String[] months = new java.text.DateFormatSymbols().getMonths();
        String userId = FirebaseAuth.getInstance().getUid();

        for (int i = 0; i < 12; i++) {
            String month = months[i] + " " + java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            double total = db.getTotalSpent(month, userId);

            entries.add(new BarEntry(i, (float) total));
            labels.add(months[i].substring(0, 3)); // Jan, Feb, Mar...
        }

        BarDataSet dataSet = new BarDataSet(entries, "Monthly Expense");
        dataSet.setColors(com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);

        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.invalidate();
    }


}
