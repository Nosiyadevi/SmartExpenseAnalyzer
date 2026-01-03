package com.example.smartexpenseanalyzer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.activity.ComponentActivity;


public class AddExpenseActivity extends AppCompatActivity {

    EditText etAmount, etCategory;
    Button btnSave;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        btnSave = findViewById(R.id.btnSave);

        db = new DBHelper(this);

        btnSave.setOnClickListener(v -> {

            String amountStr = etAmount.getText().toString().trim();
            String category = etCategory.getText().toString().trim();

            if (amountStr.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String month = new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date());
            String userId = FirebaseAuth.getInstance().getUid();

            db.insertExpense(amount, category, month, userId);

            Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
