package com.example.expensetrackingapplication_coincontrol;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private SessionManager sessionManager;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "Signup.db", null, 4);
        sessionManager = new SessionManager(context);
    }

    public String getCurrentUsername() {
        return sessionManager.getUsername();
    }

    public static final String databaseName = "Signup.db";

    // Method to create the allusers & monthly_budget table
    @Override
    public void onCreate(SQLiteDatabase MyDatabase) {
        Log.d("DatabaseHelper", "onCreate: Creating table 'allusers'");
        MyDatabase.execSQL("CREATE TABLE allusers (name TEXT, username TEXT PRIMARY KEY, email TEXT, phone TEXT, password TEXT)");
    }

    // Method to upgrade the allusers & monthly_budget table
    @Override
    public void onUpgrade(SQLiteDatabase MyDatabase, int oldVersion, int newVersion) {
        MyDatabase.execSQL("DROP TABLE IF EXISTS allusers");
        onCreate(MyDatabase);
    }

    public Boolean insertData(String name, String username, String email, String phone, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("phone", phone);
        contentValues.put("password", password);
        long result = MyDatabase.insert("allusers", null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Boolean checkUsername(String username) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from allusers where username = ?", new String[]{username});

        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean checkUsernamePassword(String username, String password) {
        SQLiteDatabase MyDatabase = this.getWritableDatabase();
        Cursor cursor = MyDatabase.rawQuery("Select * from allusers where username = ? and password = ?", new String[]{username, password});

        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    public UserProfile getUserProfile(String username) {
        if (username == null) {
            return null; // or handle appropriately
        }

        SQLiteDatabase db = this.getReadableDatabase();
        UserProfile userProfile = null;

        Log.d("DatabaseHelper", "Executing query: SELECT * FROM allusers WHERE username='" + username + "'");

        Cursor cursor = db.rawQuery("SELECT * FROM allusers WHERE username=?", new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            // Populate the UserProfile object with retrieved data
            userProfile = new UserProfile();
            userProfile.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            userProfile.setName(cursor.getString(cursor.getColumnIndex("name")));
            userProfile.setEmail(cursor.getString(cursor.getColumnIndex("email")));
            userProfile.setPhone(cursor.getString(cursor.getColumnIndex("phone")));

            // Log cursor count and column values
            Log.d("DatabaseHelper", "Cursor count: " + cursor.getCount());
            Log.d("DatabaseHelper", "Name: " + userProfile.getName());
            Log.d("DatabaseHelper", "Email: " + userProfile.getEmail());
            Log.d("DatabaseHelper", "Phone: " + userProfile.getPhone());
        } else {
            Log.d("DatabaseHelper", "Cursor is empty or null");
        }

        if (cursor != null) {
            cursor.close(); // Close the cursor
        }
        db.close(); // Close the database

        return userProfile;
    }

    // New method to trigger database creation
    public void createDatabase() {
        // This line triggers database creation
        SQLiteDatabase db = this.getWritableDatabase();
        db.close(); // Close the database to free up resources
    }
}
