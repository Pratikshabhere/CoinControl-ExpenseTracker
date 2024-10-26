package com.example.expensetrackingapplication_coincontrol;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class YearlyBudgetDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "yearly_budget.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_YEARLY_BUDGET = "yearly_budget";

    // Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_YEARLY_EXPENSE = "yearly_expense";

    // SQL statement to create the table
    private static final String SQL_CREATE_YEARLY_BUDGET_TABLE = "CREATE TABLE " +
            TABLE_YEARLY_BUDGET + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_YEAR + " INTEGER," +
            COLUMN_YEARLY_EXPENSE + " REAL)";

    public YearlyBudgetDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_YEARLY_BUDGET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handles database upgrades if needed
    }

    public void insertYearlyExpense(int year, float yearlyExpense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_YEAR, year);
        values.put(COLUMN_YEARLY_EXPENSE, yearlyExpense);

        long newRowId = db.insert(TABLE_YEARLY_BUDGET, null, values);
        db.close();
    }
}

