package com.example.expensetrackingapplication_coincontrol;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensetrackingapplication_coincontrol.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // Hide the ActionBar
        setContentView(R.layout.activity_welcome);  // Set the content view

        // Finding the buttons by their IDs
        Button loginButton = findViewById(R.id.welcome_signin);
        Button signupButton = findViewById(R.id.welcome_signup);

        // Creating a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.black2));
        drawable.setCornerRadius(20); // Set radius to 0 to make all corners angular

        // Applying the custom background drawable to the buttons
        loginButton.setBackground(drawable);
        signupButton.setBackground(drawable);

        // Setting click listeners for the buttons
        loginButton.setOnClickListener(view -> openLoginActivity());
        signupButton.setOnClickListener(view -> openSignupActivity());
    }

    private void openLoginActivity() {
        // Starting the LoginActivity when the login button is clicked
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void openSignupActivity() {
        // Starting the SignupActivity when the signup button is clicked
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }
}
