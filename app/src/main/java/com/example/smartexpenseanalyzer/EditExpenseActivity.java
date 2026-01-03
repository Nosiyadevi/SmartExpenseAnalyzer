package com.example.smartexpenseanalyzer;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;
public class EditExpenseActivity extends AppCompatActivity {

    EditText etAmount, etCategory;
    Button btnUpdate;
    DBHelper db;
    int expenseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);

        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        btnUpdate = findViewById(R.id.btnUpdate);

        db = new DBHelper(this);

        expenseId = getIntent().getIntExtra("id", -1);
        double amount = getIntent().getDoubleExtra("amount", 0);
        String category = getIntent().getStringExtra("category");

        etAmount.setText(String.valueOf(amount));
        etCategory.setText(category);

        btnUpdate.setOnClickListener(v -> {
            double newAmount = Double.parseDouble(etAmount.getText().toString());
            String newCategory = etCategory.getText().toString();
            String userId = FirebaseAuth.getInstance().getUid();

            db.updateExpense(expenseId, newAmount, newCategory, userId);
            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
