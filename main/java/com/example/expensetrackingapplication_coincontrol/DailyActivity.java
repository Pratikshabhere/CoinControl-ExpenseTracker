package com.example.expensetrackingapplication_coincontrol;

import static com.example.expensetrackingapplication_coincontrol.YearlyActivity.openDrawer;
import static com.example.expensetrackingapplication_coincontrol.YearlyActivity.redirectActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.expensetrackingapplication_coincontrol.databinding.ActivityDailyBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DailyActivity extends AppCompatActivity {

    private static final int REQUEST_SPEECH_RECOGNITION = 1;
    ActivityDailyBinding binding;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout profile, daily, monthly, yearly, logout;
    EditText editTextSalary, editTextFood, editTextGroceries, editTextTravel,
            editTextElectricity, editTextEducation, editTextShopping;
    TextView textViewEstimatedSalary, textViewRemaining, textViewSpending;
    PieChart pieChart;
    BarChart barChart;
    private final List<Float> displayPieChart = new ArrayList<>();
    private final List<Float> updateBarChart = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // Hide the ActionBar
        setContentView(R.layout.activity_daily);
        binding = ActivityDailyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Find the EditText fields and Button
        EditText salaryEditText = findViewById(R.id.editTextSalary);
        EditText foodEditText = findViewById(R.id.editTextFood);
        EditText groceriesEditText = findViewById(R.id.editTextGroceries);
        EditText travelEditText = findViewById(R.id.editTextTravel);
        EditText electricityEditText = findViewById(R.id.editTextElectricity);
        EditText educationEditText = findViewById(R.id.editTextEducation);
        EditText shoppingEditText = findViewById(R.id.editTextShopping);
        Button calculateButton = findViewById(R.id.buttonCalculate);

        // Applying custom background drawables to EditText fields
        applyCustomBackgroundDrawable(salaryEditText);
        applyCustomBackgroundDrawable(foodEditText);
        applyCustomBackgroundDrawable(groceriesEditText);
        applyCustomBackgroundDrawable(travelEditText);
        applyCustomBackgroundDrawable(electricityEditText);
        applyCustomBackgroundDrawable(educationEditText);
        applyCustomBackgroundDrawable(shoppingEditText);

        // Applying custom background drawable to Button
        applyCustomBackgroundDrawableToButton(calculateButton);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        profile = findViewById(R.id.profile);
        daily = findViewById(R.id.daily_budget);
        monthly = findViewById(R.id.monthly_budget);
        yearly = findViewById(R.id.yearly_budget);
        logout = findViewById(R.id.logout);

        editTextSalary = findViewById(R.id.editTextSalary);
        editTextFood = findViewById(R.id.editTextFood);
        editTextGroceries = findViewById(R.id.editTextGroceries);
        editTextTravel = findViewById(R.id.editTextTravel);
        editTextElectricity = findViewById(R.id.editTextElectricity);
        editTextEducation = findViewById(R.id.editTextEducation);
        editTextShopping = findViewById(R.id.editTextShopping);

        textViewEstimatedSalary = findViewById(R.id.textViewEstimatedSalary);
        textViewRemaining = findViewById(R.id.textViewRemaining);
        textViewSpending = findViewById(R.id.textViewSpending);

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(DailyActivity.this, MainActivity.class);
            }
        });
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(DailyActivity.this, MonthlyActivity.class);
            }
        });
        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(DailyActivity.this, YearlyActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the welcome page
                redirectActivity(DailyActivity.this, WelcomeActivity.class);
                // Displaying a logout toast
                Toast.makeText(DailyActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.buttonCalculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateBudget();
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
    private void logoutUser(DailyActivity dailyActivity) {
        // navigate to login screen
        Intent intent = new Intent(DailyActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // Closing the current activity to prevent going back to it on back press
    }

    private float getFloatFromEditText(EditText editText) {
        String text = editText.getText().toString();
        return text.isEmpty() ? 0.0f : Float.parseFloat(text);
    }

    private void applyCustomBackgroundDrawable(EditText editText) {
        // Create a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.light_grey));
        drawable.setCornerRadius(20); // Set radius to control the degree of rounding

        // Setting the custom drawable as the background for the EditText
        editText.setBackground(drawable);
    }
    private void applyCustomBackgroundDrawableToButton(Button button) {
        // Creating a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.blue_1)); // Change color as needed
        drawable.setCornerRadius(20); // Set radius to 20dp for example, adjust as needed

        // Setting the custom drawable as the background for the Button
        button.setBackground(drawable);
    }

    private void calculateBudget() {
        // Validating inputs before proceeding
        if (!validateInputs()) {
            return; // Exit method if validation fails
        }

        // Getting salary and expenses
        float salary = Float.parseFloat(editTextSalary.getText().toString());
        float food = Float.parseFloat(editTextFood.getText().toString());
        float groceries = Float.parseFloat(editTextGroceries.getText().toString());
        float travel = Float.parseFloat(editTextTravel.getText().toString());
        float electricity = Float.parseFloat(editTextElectricity.getText().toString());
        float education = Float.parseFloat(editTextEducation.getText().toString());
        float shopping = Float.parseFloat(editTextShopping.getText().toString());

        // Calculating estimated salary, remaining, and spending
        float estimatedSalary = salary ;
        float remaining = salary - (food + groceries + travel + electricity + education + shopping);
        float spending = salary - remaining;

        // Display results
        textViewEstimatedSalary.setText("Estimated Budget: " + estimatedSalary);
        textViewRemaining.setText("Remaining: " + remaining);
        textViewSpending.setText("Spending: " + spending);

        // Displaying Pie Chart and Bar Chart
        displayPieChart(food, groceries, travel, electricity, education, shopping);
        updateBarChart(food, groceries, travel, electricity, education, shopping);

        // Checking if remaining budget is negative
        if (remaining < 0) {
            // Display alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Budget Alert");
            builder.setMessage("Expenses have exceeded the salary!");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void displayPieChart(float food, float groceries, float travel, float electricity, float education, float shopping) {
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(30f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(food, "Food"));
        entries.add(new PieEntry(groceries, "Groceries"));
        entries.add(new PieEntry(travel, "Travel"));
        entries.add(new PieEntry(electricity, "Electricity"));
        entries.add(new PieEntry(education, "Education"));
        entries.add(new PieEntry(shopping, "Shopping"));

        PieDataSet dataSet = new PieDataSet(entries, "Expense Categories");
        dataSet.setColors(Color.rgb(255, 102, 102), Color.rgb(255, 204, 102),
                Color.rgb(153, 204, 255), Color.rgb(255, 102, 204),
                Color.rgb(102, 255, 102), Color.rgb(255, 255, 102));

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.invalidate(); // Refresh the chart
    }

    private void updateBarChart(float food, float groceries, float travel, float electricity, float education, float shopping) {
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, food));
        barEntries.add(new BarEntry(2, groceries));
        barEntries.add(new BarEntry(3, travel));
        barEntries.add(new BarEntry(4, electricity));
        barEntries.add(new BarEntry(5, education));
        barEntries.add(new BarEntry(6, shopping));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Expense Categories");

        barDataSet.setColors(Color.rgb(255, 102, 102), Color.rgb(255, 204, 102),
                Color.rgb(153, 204, 255), Color.rgb(255, 102, 204),
                Color.rgb(102, 255, 102), Color.rgb(255, 255, 102));

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        barChart.invalidate(); // Refresh the chart
    }

    private void displayCharts(float food, float groceries, float travel, float electricity, float education, float shopping) {
        displayPieChart(food, groceries, travel, electricity, education, shopping);
        updateBarChart(food, groceries, travel, electricity, education, shopping);
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
        command = command.toLowerCase();
        if (command.contains("navigate") && command.contains("daily")) {
            redirectActivity(DailyActivity.this, DailyActivity.class);
        }else if (command.contains("navigate") && command.contains("profile")) {
            redirectActivity(DailyActivity.this, MainActivity.class);
        } else if (command.contains("navigate") && command.contains("monthly")) {
            redirectActivity(DailyActivity.this, MonthlyActivity.class);
        } else if (command.contains("navigate") && command.contains("yearly")) {
            redirectActivity(DailyActivity.this, YearlyActivity.class);
        } else if (command.contains("salary")) {
            // Extracting the salary amount from the command
            float salary = extractSalary(command);

            // Updating the EditText field for salary with the extracted amount
            editTextSalary.setText(String.valueOf(salary));
        }else if (command.contains("budget") && command.contains("for")) {
            // Extracting the expense category and budget amount from the command
            String category = extractCategory(command);
            float amount = extractAmount(command);

            // Updating the corresponding EditText field with the budget amount
            switch (category) {
                case "food":
                    editTextFood.setText(String.valueOf(amount));
                    break;
                case "groceries":
                    editTextGroceries.setText(String.valueOf(amount));
                    break;
                case "travel":
                    editTextTravel.setText(String.valueOf(amount));
                    break;
                case "education":
                    editTextElectricity.setText(String.valueOf(amount));
                    break;
                case "shopping":
                    editTextEducation.setText(String.valueOf(amount));
                    break;
                case "savings":
                    editTextShopping.setText(String.valueOf(amount));
                    break;
            }
        }else if (command.contains("calculate") && command.contains("budget")) {
            calculateBudget();
        } else if (command.contains("logout")) {
            // to log out the user
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            logoutUser(this);
        } else {
            Toast.makeText(this, "Command not recognized", Toast.LENGTH_SHORT).show();
        }

    }

    // Method to extract the salary amount from the command
    private float extractSalary(String command) {
        String[] parts = command.split(" ");
        if (parts.length >= 3) {
            try {
                return Float.parseFloat(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Handling invalid number format
                return 0.0f;
            }
        } else {
            // Handling invalid command format
            return 0.0f;
        }
    }

    // Method to extract the expense category from the command
    private String extractCategory(String command) {
        if (command.contains("food")) {
            return "food";
        } else if (command.contains("groceries")) {
            return "groceries";
        } else if (command.contains("travel")) {
            return "travel";
        } else if (command.contains("education")) {
            return "education";
        } else if (command.contains("shopping")) {
            return "shopping";
        } else if (command.contains("saving")) {
            return "savings";
        } else {
            // Handling unrecognized category or return a default category
            return "default";
        }
    }

    // Method to extract the budget amount from the command
    private float extractAmount(String command) {
        String[] parts = command.split(" ");
        if (parts.length >= 4) {
            try {
                return Float.parseFloat(parts[parts.length - 1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                // Handling invalid number format
                return 0.0f;
            }
        } else {
            // Handling invalid command format
            return 0.0f;
        }
    }

    private boolean validateInputs() {
        // Getting text from EditText fields
        String salaryText = editTextSalary.getText().toString().trim();
        String foodText = editTextFood.getText().toString().trim();
        String groceriesText = editTextGroceries.getText().toString().trim();
        String travelText = editTextTravel.getText().toString().trim();
        String electricityText = editTextElectricity.getText().toString().trim();
        String educationText = editTextEducation.getText().toString().trim();
        String shoppingText = editTextShopping.getText().toString().trim();

        // Initializing a flag to track if any validation fails
        boolean validationFailed = false;

        // Checking if any field is empty
        if (salaryText.isEmpty() || foodText.isEmpty() || groceriesText.isEmpty() ||
                travelText.isEmpty() || electricityText.isEmpty() || educationText.isEmpty() || shoppingText.isEmpty()) {
            showToast("A field is empty");
            validationFailed = true;
        }

        // Validating each field to contain only numerical values
        if (!isNumeric(salaryText) || !isNumeric(foodText) || !isNumeric(groceriesText) ||
                !isNumeric(travelText) || !isNumeric(electricityText) || !isNumeric(educationText) || !isNumeric(shoppingText)) {
            showToast("Enter a Numerical value");
            validationFailed = true;
        }

        // false if any validation failed, else return true
        return !validationFailed;
    }

    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Float.parseFloat(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
