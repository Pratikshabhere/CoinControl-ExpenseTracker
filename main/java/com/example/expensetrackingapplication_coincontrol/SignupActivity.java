package com.example.expensetrackingapplication_coincontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensetrackingapplication_coincontrol.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    DatabaseHelper databaseHelper;

    CheckBox showPasswordCheckbox;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // Hide the ActionBar
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        // Initializing your views after inflating the binding
        showPasswordCheckbox = binding.showPasswordCheckbox;
        passwordEditText = binding.signupPassword;

        // Applying a custom background drawable with angular corners to the EditText fields
        applyCustomBackgroundDrawable(binding.signupName);
        applyCustomBackgroundDrawable(binding.signupUsername);
        applyCustomBackgroundDrawable(binding.signupEmail);
        applyCustomBackgroundDrawable(binding.signupPhone);
        applyCustomBackgroundDrawable(binding.signupPassword);
        applyCustomBackgroundDrawableToButton(binding.signupButton);

        // Setting onClickListener for the showPasswordCheckbox
        showPasswordCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle password visibility
                if (showPasswordCheckbox.isChecked()) {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Moving the cursor to the end of the text to ensure it's visible
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        binding.signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieving input values when the user clicks the button
                String name = binding.signupName.getText().toString();
                String username = binding.signupUsername.getText().toString();
                String email = binding.signupEmail.getText().toString();
                String phone = binding.signupPhone.getText().toString();
                String password = binding.signupPassword.getText().toString();

                // Validating inputs
                if (!validateInputs(name, username, email, phone, password)) {
                    return;
                }

                Log.d("SignupActivity", "Validation passed. Proceeding with signup");

                // Database operations
                boolean insert = databaseHelper.insertData(name, username, email, phone, password);

                if (insert) {
                    showToast("Signup Successfully");
                    Log.d("SignupActivity", "Signup Successfully");

                    // Redirecting to login activity
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                } else {
                    showToast("Signup Failed");
                    Log.d("SignupActivity", "Signup Failed");
                }
            }

            private boolean validateInputs(String name, String username, String email, String phone, String password) {
                // Validating name
                if (name.isEmpty()) {
                    showToast("Name cannot be empty");
                    return false;
                }

                // Validating username
                if (username.isEmpty()) {
                    showToast("Username cannot be empty");
                    return false;
                }

                // Validate phone
                if (phone.isEmpty()) {
                    showToast("Phone cannot be empty");
                    return false;
                }

                // Checking if phone contains only digits
                if (!TextUtils.isDigitsOnly(phone)) {
                    showToast("Invalid phone number. Only digits are allowed");
                    return false;
                }

                // Checking if phone has exactly 10 digits
                if (phone.length() != 10) {
                    showToast("Invalid phone number. Must be 10 digits");
                    return false;
                }

                // Validating email
                if (email.isEmpty()) {
                    showToast("Email cannot be empty");
                    return false;
                }

                // Checking if email ends with @gmail.com
                if (!email.toLowerCase().endsWith("@gmail.com")) {
                    showToast("Invalid email format. Must end with @gmail.com");
                    return false;
                }

                return true;  // Return false if any validation fails
            }

            private void showToast(String message) {
                Toast.makeText(SignupActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        binding.loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
    private void applyCustomBackgroundDrawable(EditText editText) {
        // Creating a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.light_grey));
        drawable.setCornerRadius(20); // Setting radius to 0 to make all corners angular

        // Setting the custom drawable as the background for the EditText
        editText.setBackground(drawable);
    }
    private void applyCustomBackgroundDrawableToButton(Button button) {
        // Creating a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.blue_1)); // Changing color as needed
        drawable.setCornerRadius(20); // Setting radius to 20dp for example, adjust as needed

        // Setting the custom drawable as the background for the Button
        button.setBackground(drawable);
    }
}
