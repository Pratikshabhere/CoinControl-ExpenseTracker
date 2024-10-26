package com.example.expensetrackingapplication_coincontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensetrackingapplication_coincontrol.databinding.ActivityMonthlyBinding;
import com.example.expensetrackingapplication_coincontrol.databinding.ActivityYearlyBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class YearlyActivity extends AppCompatActivity {

    ActivityYearlyBinding binding;

    private YearlyBudgetDatabaseHelper dbHelper;

    private static final int REQUEST_SPEECH_RECOGNITION = 1;

    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout profile, daily, monthly, yearly, logout;

    EditText editTextExpense;
    Button buttonAddExpense;
    BarChart barChart;
    TextView textViewTotalExpense;

    List<BarEntry> yearlyData;

    private List<Float> yearlyExpenses = new ArrayList<>();
    private final int[] BRIGHT_COLORS = new int[]{
            Color.rgb(255, 102, 102),
            Color.rgb(255, 204, 102),
            Color.rgb(255, 255, 102),
            Color.rgb(102, 255, 102),
            Color.rgb(102, 204, 255),
            Color.rgb(204, 102, 255),
            // Add more colors if needed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // Hide the ActionBar
        setContentView(R.layout.activity_yearly);
        binding = ActivityYearlyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the database helper
        dbHelper = new YearlyBudgetDatabaseHelper(this);

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        profile = findViewById(R.id.profile);
        daily = findViewById(R.id.daily_budget);
        monthly = findViewById(R.id.monthly_budget);
        yearly = findViewById(R.id.yearly_budget);
        logout = findViewById(R.id.logout);

        editTextExpense = findViewById(R.id.editTextYearlyExpense);
        buttonAddExpense = findViewById(R.id.buttonAddYearlyExpense);
        barChart = findViewById(R.id.barChartYearly);
        textViewTotalExpense = findViewById(R.id.textViewTotalYearlyExpense);

        // finding EditText fields by their IDs
        EditText yearlyexpenseEditText = findViewById(R.id.editTextYearlyExpense);

        // Applying a custom background drawable with angular corners to the EditText fields
        applyCustomBackgroundDrawable(yearlyexpenseEditText);
        applyCustomBackgroundDrawableToButton(binding.buttonAddYearlyExpense);

        // Initializing the Spinner
        Spinner yearSpinner = findViewById(R.id.yearSpinner);

        // Creating an ArrayAdapter using the string array and a custom layout for dropdown items
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.years_array, R.layout.spinner_dropdown_yearly_item);

        // Specifying the layout to use for the dropdown items
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_yearly_item);

        // Applying the adapter to the spinner
        yearSpinner.setAdapter(adapter);
        yearlyData = new ArrayList<>();

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(YearlyActivity.this, MainActivity.class);
            }
        });
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(YearlyActivity.this, DailyActivity.class);
            }
        });
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(YearlyActivity.this, MonthlyActivity.class);
            }
        });
        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigating to the welcome page
                redirectActivity(YearlyActivity.this, WelcomeActivity.class);
                // Display a logout toast
                Toast.makeText(YearlyActivity.this, "Logout", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExpense();
            }
        });

        FloatingActionButton fabVoiceAssistant = findViewById(R.id.fabVoiceAssistant);
        fabVoiceAssistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSpeechRecognition();
            }
        });

        updateChart();
    }

    private void logoutUser(YearlyActivity dailyActivity) {
        // logic to clear user session, navigate to login screen
        Intent intent = new Intent(YearlyActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // Closing the current activity to prevent going back to it on back press
    }

    private void addExpense() {
        String expenseText = editTextExpense.getText().toString();
        if (!expenseText.isEmpty()) {
            float expense = Float.parseFloat(expenseText);
            int selectedYear = getSelectedYear(); // this method used to get the selected year
            yearlyExpenses.add(expense);
            editTextExpense.getText().clear();
            dbHelper.insertYearlyExpense(selectedYear, expense);
            updateChart();
        }
    }

    private int getSelectedYear() {
        Spinner yearSpinner = findViewById(R.id.yearSpinner);
        return Integer.parseInt(yearSpinner.getSelectedItem().toString());
    }

    private void updateChart() {
        updateYearlyPieChart();
        updateYearlyBarChart();
        calculateAndDisplayTotalExpense();
    }

    private void updateYearlyPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i < yearlyExpenses.size(); i++) {
            pieEntries.add(new PieEntry(yearlyExpenses.get(i), "Year " + (i + 1)));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Yearly Expenses");
        dataSet.setColors(BRIGHT_COLORS);

        PieData data = new PieData(dataSet);

        PieChart pieChart = findViewById(R.id.pieChartYearly);
        pieChart.setData(data);
        pieChart.setDescription(null);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.invalidate();
    }

    private void updateYearlyBarChart() {
        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < yearlyExpenses.size(); i++) {
            barEntries.add(new BarEntry(i, yearlyExpenses.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Yearly Expenses");
        dataSet.setColors(BRIGHT_COLORS);

        BarData data = new BarData(dataSet);

        BarChart barChart = findViewById(R.id.barChartYearly);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getYearLabels()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.invalidate();
    }

    private List<String> getYearLabels() {
        List<String> yearLabels = new ArrayList<>();
        for (int i = 1; i <= yearlyExpenses.size(); i++) {
            yearLabels.add("Year " + i);
        }
        return yearLabels;
    }

    private float calculateTotalExpense() {
        float total = 0.0f;
        for (Float expense : yearlyExpenses) {
            total += expense;
        }
        return total;
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity) {
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
        command = command.toLowerCase(); // Converting the command to lowercase for case-insensitivity

        if (command.contains("navigate") && command.contains("daily")) {
            redirectActivity(YearlyActivity.this, DailyActivity.class);
        } else if (command.contains("navigate") && command.contains("monthly")) {
            redirectActivity(YearlyActivity.this, MonthlyActivity.class);
        } else if (command.contains("navigate") && command.contains("yearly")) {
            redirectActivity(YearlyActivity.this, YearlyActivity.class);
        }else if (command.contains("navigate") && command.contains("profile")) {
            redirectActivity(YearlyActivity.this, MainActivity.class);
        } else if (command.contains("budget")) {
            // Extracting the budget amount from the spoken command
            float budgetAmount = addBudgetAmountFromCommand(command);

            // Checking if the budget amount is valid
            if (budgetAmount > 0) {
                // Updating the budget EditText field with the budget amount
                editTextExpense.setText(String.valueOf(budgetAmount));
            } else {
                // Handling case where budget amount is not valid
                Toast.makeText(this, "Invalid budget amount", Toast.LENGTH_SHORT).show();
            }
        } else if (command.contains("expense")) {
            // Simulating a click on the "Add Expense" button
            buttonAddExpense.performClick();
        } else if (command.contains("logout")) {
            // Implementing logic to log out the user
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
    private void applyCustomBackgroundDrawable(EditText editText) {
        // Create a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.light_grey));
        drawable.setCornerRadius(20); // Set radius to control the degree of rounding

        // Set the custom drawable as the background for the EditText
        editText.setBackground(drawable);
    }
    private void applyCustomBackgroundDrawableToButton(Button button) {
        // Create a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.blue_1)); // Change color as needed
        drawable.setCornerRadius(20); // Set radius to 20dp for example, adjust as needed

        // Set the custom drawable as the background for the Button
        button.setBackground(drawable);
    }
    private float addBudgetAmountFromCommand(String command) {
        // Split the command into words
        String[] words = command.split(" ");

        // Search for the budget amount in the words
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("budget") && i < words.length - 1) {
                // Check if the next word is a valid number representing the budget amount
                try {
                    return Float.parseFloat(words[i + 1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return -1; // Invalid budget amount
                }
            }
        }

        return -1; // Budget amount not found
    }
    private void calculateAndDisplayTotalExpense() {
        // Calculate the total expense from the monthly expenses list
        float totalExpense = calculateTotalExpense();

        // Update the TextView to display the total expense
        textViewTotalExpense.setText("Total Expense: " + totalExpense);
    }
}
