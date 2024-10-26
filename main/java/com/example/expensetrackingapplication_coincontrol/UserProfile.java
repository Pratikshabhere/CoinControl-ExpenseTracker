package com.example.expensetrackingapplication_coincontrol;

import android.database.Cursor;

public class UserProfile {
    private String username;
    private String name;
    private String email;
    private String phone;

    public UserProfile() {
    }

    // Constructor for creating UserProfile from a Cursor
    public UserProfile(String username, String name, String email, String phone) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    public UserProfile(Cursor cursor) {
        if (cursor != null) {
            int usernameIndex = cursor.getColumnIndex("username");
            int nameIndex = cursor.getColumnIndex("name");
            int emailIndex = cursor.getColumnIndex("email");
            int phoneIndex = cursor.getColumnIndex("phone");

            // Checking if columns exist before retrieving values
            if (usernameIndex != -1) {
                this.username = cursor.getString(usernameIndex);
            }
            if (nameIndex != -1) {
                this.name = cursor.getString(nameIndex);
            }
            if (emailIndex != -1) {
                this.email = cursor.getString(emailIndex);
            }
            if (phoneIndex != -1) {
                this.phone = cursor.getString(phoneIndex);
            }
        }
    }

    // Getter methods

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}