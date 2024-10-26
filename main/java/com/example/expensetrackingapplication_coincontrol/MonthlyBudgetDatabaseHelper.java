package com.example.expensetrackingapplication_coincontrol;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MonthlyBudgetDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "monthly_budget.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_MONTHLY_BUDGET = "monthly_budget";

    // Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MONTH = "month";
    private static final String COLUMN_MONTHLY_EXPENSE = "monthly_expense";

    // SQL statement to create the table
    private static final String SQL_CREATE_MONTHLY_BUDGET_TABLE = "CREATE TABLE " +
            TABLE_MONTHLY_BUDGET + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_MONTH + " TEXT," +
            COLUMN_MONTHLY_EXPENSE + " REAL)";

    public MonthlyBudgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void insertMonthlyExpense(float expense, String month) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MONTH, month); // Insert the month value
        values.put(COLUMN_MONTHLY_EXPENSE, expense); // Insert the expense value

        // Inserting new row, returning the primary key value of the new row
        long newRowId = db.insert(TABLE_MONTHLY_BUDGET, null, values);
        db.close(); // Close the database connection
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MONTHLY_BUDGET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handling database upgrades if needed
    }
}
