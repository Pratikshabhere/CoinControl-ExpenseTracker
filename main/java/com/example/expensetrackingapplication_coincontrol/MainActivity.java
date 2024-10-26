package com.example.expensetrackingapplication_coincontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SPEECH_RECOGNITION = 1;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout profile, daily, monthly, yearly, logout;
    Button editProfileButton, saveProfileButton;
    EditText profileName, profileDOB, profileGender, profileEmail, profilePhone;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // Hide the ActionBar
        setContentView(R.layout.activity_main);

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Assuming you have these EditText fields in your MainActivity
        EditText usernameEditText = findViewById(R.id.profile_name);
        EditText emailEditText = findViewById(R.id.profile_email);
        EditText phoneEditText = findViewById(R.id.profile_phone);

        // Fetch user profile based on the current username
        String currentUsername = databaseHelper.getCurrentUsername();
        UserProfile userProfile = databaseHelper.getUserProfile(currentUsername);

        // Log the retrieved username
        Log.d("MainActivity", "Current Username: " + currentUsername);

        // Check if the user profile is not null
        if (userProfile != null) {
            // Set the retrieved values to the EditText fields
            usernameEditText.setText(userProfile.getUsername());
            emailEditText.setText(userProfile.getEmail());
            phoneEditText.setText(userProfile.getPhone());
        } else {
            // Handle the case where the user profile is null (optional)
            Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show();
        }

        // Retrieve the username from the intent
        String username = getIntent().getStringExtra("username");

        // In your onCreate method after initializing views
        displayUserProfile(username);
        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        profile = findViewById(R.id.profile);
        daily = findViewById(R.id.daily_budget);
        monthly = findViewById(R.id.monthly_budget);
        yearly = findViewById(R.id.yearly_budget);
        logout = findViewById(R.id.logout);
        editProfileButton = findViewById(R.id.editProfileButton);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        profileName = findViewById(R.id.profile_name);
        profileDOB = findViewById(R.id.profile_dob);
        profileGender = findViewById(R.id.profile_gender);
        profileEmail = findViewById(R.id.profile_email);
        profilePhone = findViewById(R.id.profile_phone);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(MainActivity.this, DailyActivity.class);
            }
        });
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(MainActivity.this, MonthlyActivity.class);
            }
        });
        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(MainActivity.this, YearlyActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the welcome page (assuming WelcomeActivity represents the welcome page)
                redirectActivity(MainActivity.this, WelcomeActivity.class);
                // Display a logout toast
                Toast.makeText(MainActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fabVoiceAssistant = findViewById(R.id.fabVoiceAssistant);
        fabVoiceAssistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognition();
            }
        });

    }

    private void disableEditMode() {
        EditText profileNameEditText = findViewById(R.id.profile_name);

        // Check if the EditText is not null before calling setEnabled
        if (profileNameEditText != null) {
            profileNameEditText.setEnabled(false);
        }
        // Repeat the above pattern for other EditText objects
    }

    private String getCurrentUsername() {
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        String currentUsername = dbHelper.getCurrentUsername();
        dbHelper.close();
        return currentUsername;
    }

    private void displayUserProfile(String username) {
        // Fetch user data and populate the EditText fields
        // For now, let's assume user data is retrieved from a UserProfile object
        UserProfile userProfile = getUserData(username);  // Use the received username

        if (userProfile != null) {
            profileName.setText(userProfile.getUsername());
            profileEmail.setText(userProfile.getEmail());
            profilePhone.setText(userProfile.getPhone());
        } else {
            Toast.makeText(this, "User profile not found", Toast.LENGTH_SHORT).show();
        }
    }

    private UserProfile getUserData(String username) {
        DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this);
        UserProfile userProfile = dbHelper.getUserProfile(username);
        dbHelper.close();

        return userProfile;
    }


    private void enableEditMode() {
        // Enable editing in your EditText fields
        profileName.setEnabled(true);
        profileDOB.setEnabled(true);
        profileGender.setEnabled(true);
        profileEmail.setEnabled(true);
        profilePhone.setEnabled(true);
    }

    private void logoutUser(MainActivity dailyActivity) {
        // Implement logic to clear user session, navigate to login screen, etc.
        // For demonstration purposes, let's just navigate back to the login screen
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // Close the current activity to prevent going back to it on back press
    }

    private void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void redirectActivity(Activity activity, Class secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SPEECH_RECOGNITION && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && !matches.isEmpty()) {
                String command = matches.get(0);
                processVoiceCommand(command);
            }
        }
    }

    private void processVoiceCommand(String command) {
        command = command.toLowerCase(); // Convert the command to lowercase for case-insensitivity

        if (command.contains("navigate") && command.contains("daily")) {
            redirectActivity(MainActivity.this, DailyActivity.class);
        } else if (command.contains("navigate") && command.contains("monthly")) {
            redirectActivity(MainActivity.this, MonthlyActivity.class);
        } else if (command.contains("navigate") && command.contains("yearly")) {
            redirectActivity(MainActivity.this, YearlyActivity.class);
        }else if (command.contains("navigate") && command.contains("profile")) {
            redirectActivity(MainActivity.this, MainActivity.class);
        } else if (command.contains("add") && command.contains("budget")) {
            // Implement logic to add a budget
            Toast.makeText(this, "Adding budget...", Toast.LENGTH_SHORT).show();
        } else if (command.contains("calculate") && command.contains("budget")) {
            // Implement logic to calculate the budget
            Toast.makeText(this, "Calculating budget...", Toast.LENGTH_SHORT).show();
        } else if (command.contains("logout")) {
            // Implement logic to log out the user
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            logoutUser(this);
        } else {
            Toast.makeText(this, "Command not recognized", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        closeDrawer(drawerLayout);
    }
}
