package com.example.smartexpenseanalyzer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "expense.db";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount REAL," +
                "category TEXT," +
                "month TEXT," +
                "userId TEXT)");

        db.execSQL("CREATE TABLE budget (" +
                "userId TEXT PRIMARY KEY," +
                "amount REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS budget");
        onCreate(db);
    }

    // ================= ADD EXPENSE =================
    public void insertExpense(double amount, String category, String month, String userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        cv.put("category", category);
        cv.put("month", month);
        cv.put("userId", userId);
        db.insert("expenses", null, cv);
    }

    // ================= UPDATE EXPENSE =================
    public void updateExpense(int id, double amount, String category, String userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("amount", amount);
        cv.put("category", category);

        db.update(
                "expenses",
                cv,
                "id=? AND userId=?",
                new String[]{String.valueOf(id), userId}
        );
    }

    // ================= DELETE =================
    public void deleteExpense(int id) {
        getWritableDatabase().delete("expenses", "id=?", new String[]{String.valueOf(id)});
    }

    // ================= GET EXPENSES BY MONTH =================
    public ArrayList<Expense> getExpensesByMonth(String month, String userId) {
        ArrayList<Expense> list = new ArrayList<>();

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM expenses WHERE month=? AND userId=?",
                new String[]{month, userId}
        );

        while (c.moveToNext()) {
            list.add(new Expense(
                    c.getInt(0),
                    c.getDouble(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4)
            ));
        }
        c.close();
        return list;
    }

    // ================= TOTAL SPENT =================
    public double getTotalExpense(String userId) {
        double total = 0;
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT SUM(amount) FROM expenses WHERE userId=?",
                new String[]{userId}
        );
        if (c.moveToFirst()) total = c.getDouble(0);
        c.close();
        return total;
    }

    // ================= COUNT =================
    public int getExpenseCount(String userId) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM expenses WHERE userId=?",
                new String[]{userId}
        );
        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();
        return count;
    }

    // ================= BUDGET =================
    public void saveBudget(String userId, double amount) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("budget", "userId=?", new String[]{userId});

        ContentValues cv = new ContentValues();
        cv.put("userId", userId);
        cv.put("amount", amount);
        db.insert("budget", null, cv);
    }

    public double getBudget(String userId) {
        double budget = 0;
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT amount FROM budget WHERE userId=?",
                new String[]{userId}
        );
        if (c.moveToFirst()) budget = c.getDouble(0);
        c.close();
        return budget;
    }
    // TOTAL SPENT BY MONTH
    public double getTotalSpent(String month, String userId) {
        double total = 0;

        Cursor c = getReadableDatabase().rawQuery(
                "SELECT SUM(amount) FROM expenses WHERE month=? AND userId=?",
                new String[]{month, userId}
        );

        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

}
