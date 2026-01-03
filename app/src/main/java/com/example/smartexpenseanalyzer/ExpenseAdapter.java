package com.example.smartexpenseanalyzer;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    Context context;
    List<Expense> list;
    DBHelper db;

    public ExpenseAdapter(Context context, List<Expense> list) {
        this.context = context;
        this.list = list;
        db = new DBHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_expense, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int pos) {
        Expense e = list.get(pos);
        h.amount.setText("₹" + e.amount);
        h.category.setText(e.category);

        h.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, EditExpenseActivity.class);
            i.putExtra("id", e.id);
            i.putExtra("amount", e.amount);
            i.putExtra("category", e.category);
            context.startActivity(i);
        });

        h.itemView.setOnLongClickListener(v -> {
            db.deleteExpense(e.id);
            list.remove(pos);
            notifyItemRemoved(pos);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amount, category;

        ViewHolder(View v) {
            super(v);
            amount = v.findViewById(R.id.txtAmount);
            category = v.findViewById(R.id.txtCategory);
        }
    }
}
