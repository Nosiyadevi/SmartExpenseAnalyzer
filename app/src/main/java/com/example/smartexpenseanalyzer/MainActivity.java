package com.example.smartexpenseanalyzer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    TextView txtSpent, txtLeft, txtTotalCount;
    EditText etBudget;
    ImageView imgWarning;

    MaterialButton btnSetBudget, btnAddExpense, btnViewExpense, btnLogout;

    DBHelper db;
    FirebaseAuth auth;
    double budgetPercent = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Views
        txtSpent = findViewById(R.id.txtSpent);
        txtLeft = findViewById(R.id.txtLeft);
        txtTotalCount = findViewById(R.id.txtTotalCount);
        etBudget = findViewById(R.id.etBudget);
        imgWarning = findViewById(R.id.imgWarning);

        btnSetBudget = findViewById(R.id.btnSetBudget);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnViewExpense = findViewById(R.id.btnViewExpense);
        btnLogout = findViewById(R.id.btnLogout);

        db = new DBHelper(this);

        // Warning icon click
        imgWarning.setOnClickListener(v -> {

            String title;
            String message;

            if (budgetPercent >= 100) {
                title = "Budget Exceeded!";
                message = "You have exceeded your budget. Please reduce spending.";
            }
            else if (budgetPercent >= 80) {
                title = "Warning!";
                message = "You are close to reaching your budget limit.";
            }
            else {
                title = "All Good 😊";
                message = "Your spending is within the safe range.";
            }

            new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        });

        btnSetBudget.setOnClickListener(v -> {
            String value = etBudget.getText().toString().trim();

            if (value.isEmpty()) {
                Toast.makeText(this, "Enter budget", Toast.LENGTH_SHORT).show();
                return;
            }

            db.saveBudget(auth.getUid(), Double.parseDouble(value));
            updateDashboard();
        });

        btnAddExpense.setOnClickListener(v ->
                startActivity(new Intent(this, AddExpenseActivity.class)));

        btnViewExpense.setOnClickListener(v ->
                startActivity(new Intent(this, ViewExpenseActivity.class)));

        btnLogout.setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDashboard();
    }

    private void updateDashboard() {
        String uid = auth.getUid();

        double spent = db.getTotalExpense(uid);
        double budget = db.getBudget(uid);
        int count = db.getExpenseCount(uid);

        txtSpent.setText("₹" + spent);
        txtTotalCount.setText(count + " Expenses");

        double left = budget - spent;
        if (left < 0) left = 0;
        txtLeft.setText("₹" + left);

        // show saved budget
        if (budget > 0) {
            etBudget.setText(String.valueOf((int) budget));
        }
        // ---- WARNING LOGIC ----
        if (budget <= 0) {
            imgWarning.setVisibility(View.GONE);
            return;
        }

        budgetPercent = (spent / budget) * 100;
        imgWarning.setVisibility(View.VISIBLE);

        if (budgetPercent >= 100) {
            imgWarning.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        }
        else if (budgetPercent >= 80) {
            imgWarning.setColorFilter(getResources().getColor(android.R.color.holo_orange_light));
        }
        else {
            imgWarning.setColorFilter(getResources().getColor(android.R.color.holo_green_light));
        }


    }
}
