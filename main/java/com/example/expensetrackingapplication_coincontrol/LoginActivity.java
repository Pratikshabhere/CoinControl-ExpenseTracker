package com.example.expensetrackingapplication_coincontrol;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.expensetrackingapplication_coincontrol.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;

    CheckBox showPasswordCheckbox;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // Hide the ActionBar
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        // Initializing views after inflating the binding
        showPasswordCheckbox = binding.showPasswordCheckbox;
        passwordEditText = binding.loginPassword;

        // Finding the EditText fields by their IDs
        EditText usernameEditText = findViewById(R.id.login_username);
        EditText passwordEditText = findViewById(R.id.login_password);

// Applying a custom background drawable with angular corners to the EditText fields
        applyCustomBackgroundDrawable(usernameEditText);
        applyCustomBackgroundDrawable(passwordEditText);
        applyCustomBackgroundDrawableToButton(binding.loginButton);

        showPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    passwordEditText.setTransformationMethod(null); // Show the password
                } else {
                    // Hide password
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                // Moving the cursor to the end of the text to ensure it's visible
                passwordEditText.setSelection(passwordEditText.getText().length());
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String username = binding.loginUsername.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if(username.equals("") || password.equals("")) {
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkCredentials = databaseHelper.checkUsernamePassword(username, password);

                    if(checkCredentials) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, DailyActivity.class);
                        intent.putExtra("username", username); // Pass the username to MainActivity
                        startActivity(intent);
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.signupRedirectText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void applyCustomBackgroundDrawable(EditText editText) {
        // Creating a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.light_grey));
        drawable.setCornerRadius(20); // Setting radius to control the degree of rounding

        // Setting the custom drawable as the background for the EditText
        editText.setBackground(drawable);
    }
    private void applyCustomBackgroundDrawableToButton(Button button) {
        // Create a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.blue_1)); // Changing color as needed
        drawable.setCornerRadius(20); // Setting radius to 20dp for example, adjust as needed

        // Setting the custom drawable as the background for the Button
        button.setBackground(drawable);
    }
}