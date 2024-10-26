package com.example.expensetrackingapplication_coincontrol;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.expensetrackingapplication_coincontrol.databinding.ActivityMonthlyBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class  MonthlyActivity extends AppCompatActivity {

    ActivityMonthlyBinding binding;

    // Declare the database helper instance
    private MonthlyBudgetDatabaseHelper dbHelper;
    private static final int REQUEST_SPEECH_RECOGNITION = 1;
    DrawerLayout drawerLayout;
    ImageView menu;
    LinearLayout profile, daily, monthly, yearly, logout;

    EditText editTextExpense;
    Button buttonAddExpense;
    PieChart pieChart;
    TextView textViewTotalExpense;

    private List<Float> monthlyExpenses = new ArrayList<>();

    BarChart barChartMonthly;
    List<BarEntry> monthlyData;
    Button buttonAddMonthlyExpense;
    EditText editTextMonthlyExpense;

    private final int[] BRIGHT_COLORS = new int[]{
            Color.rgb(255, 102, 102),
            Color.rgb(255, 204, 102),
            Color.rgb(255, 255, 102),
            Color.rgb(102, 255, 102),
            Color.rgb(102, 204, 255),
            Color.rgb(204, 102, 255),
            // Adding more colors if needed
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();  // Hide the ActionBar
        setContentView(R.layout.activity_monthly);
        binding = ActivityMonthlyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the database helper
        dbHelper = new MonthlyBudgetDatabaseHelper(this);

        // Calling getWritableDatabase or getReadableDatabase to create or open the database
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        drawerLayout = findViewById(R.id.drawerLayout);
        menu = findViewById(R.id.menu);
        profile = findViewById(R.id.profile);
        daily = findViewById(R.id.daily_budget);
        monthly= findViewById(R.id.monthly_budget);
        yearly = findViewById(R.id.yearly_budget);
        logout = findViewById(R.id.logout);

        editTextExpense = findViewById(R.id.editTextExpense);
        buttonAddExpense = findViewById(R.id.buttonAddExpense);
        pieChart = findViewById(R.id.pieChartMonthly);
        textViewTotalExpense = findViewById(R.id.textViewTotalExpense);

        barChartMonthly = findViewById(R.id.barChartMonthly);
        buttonAddMonthlyExpense = findViewById(R.id.buttonAddExpense);
        editTextMonthlyExpense = findViewById(R.id.editTextExpense);

        // Find the EditText fields by their IDs
        EditText monthlyexpenseEditText = findViewById(R.id.editTextExpense);

        // custom background drawable with angular corners to the EditText fields
        applyCustomBackgroundDrawable(monthlyexpenseEditText);
        applyCustomBackgroundDrawableToButton(binding.buttonAddExpense);

        // Initialize the Spinner
        Spinner monthSpinner = findViewById(R.id.monthSpinner);

        // Create an ArrayAdapter using the string array and a custom layout for dropdown items
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.months_array, R.layout.spinner_dropdown_item);

        // Specify the layout to use for the dropdown items
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        // Apply the adapter to the spinner
        monthSpinner.setAdapter(adapter);
        monthlyData = new ArrayList<>();

        buttonAddMonthlyExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMonthlyExpense();
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer(drawerLayout);
            }
        });
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(MonthlyActivity.this, MainActivity.class);
            }
        });
        daily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(MonthlyActivity.this, DailyActivity.class);
            }
        });
        monthly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        yearly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redirectActivity(MonthlyActivity.this, YearlyActivity.class);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the welcome page
                redirectActivity(MonthlyActivity.this, WelcomeActivity.class);
                // Display a logout toast
                Toast.makeText(MonthlyActivity.this, "Logout", Toast.LENGTH_SHORT).show();
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

    private void logoutUser(MonthlyActivity dailyActivity) {
        // to clear user session, navigate to login screen
        Intent intent = new Intent(MonthlyActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish(); // Close the current activity to prevent going back to it on back press
    }

    public static void openDrawer(DrawerLayout drawerLayout){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public static void closeDrawer(DrawerLayout drawerLayout){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public static void redirectActivity(Activity activity, Class secondActivity){
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }

    private void addExpense() {
        String expenseText = editTextExpense.getText().toString();
        if (!expenseText.isEmpty()) {
            float expense = Float.parseFloat(expenseText);
            String selectedMonth = getSelectedMonth();
            monthlyExpenses.add(expense);
            editTextExpense.getText().clear();
            dbHelper.insertMonthlyExpense(expense, selectedMonth);
            updateChart();
        }
    }

    private String getSelectedMonth() {
        Spinner monthSpinner = findViewById(R.id.monthSpinner);
        return monthSpinner.getSelectedItem().toString();
    }

    private void updateChart() {
        updatePieChart();
        updateBarChart();
        calculateAndDisplayTotalExpense();
    }

    private float calculateTotalExpense() {
        float total = 0.0f;
        for (Float expense : monthlyExpenses) {
            total += expense;
        }
        return total;
    }

    private void addMonthlyExpense() {
        String expenseText = editTextMonthlyExpense.getText().toString().trim();
        if (!expenseText.isEmpty()) {
            float expense = Float.parseFloat(expenseText);
            monthlyData.add(new BarEntry(monthlyData.size(), expense));
            editTextMonthlyExpense.getText().clear();
            updateBarChart();
        } else {
            Toast.makeText(this, "Please enter a valid expense", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();

        for (int i = 0; i < monthlyExpenses.size(); i++) {
            pieEntries.add(new PieEntry(monthlyExpenses.get(i), "Month " + (i + 1)));
        }

        PieDataSet dataSet = new PieDataSet(pieEntries, "Monthly Expenses");
        dataSet.setColors(BRIGHT_COLORS);

        PieData data = new PieData(dataSet);

        PieChart pieChart = findViewById(R.id.pieChartMonthly);
        pieChart.setData(data);
        pieChart.setDescription(null);
        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(30f);
        pieChart.invalidate();
    }

    private void updateBarChart() {
        List<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < monthlyExpenses.size(); i++) {
            barEntries.add(new BarEntry(i, monthlyExpenses.get(i)));
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Monthly Expenses");
        dataSet.setColors(BRIGHT_COLORS);

        BarData data = new BarData(dataSet);

        BarChart barChart = findViewById(R.id.barChartMonthly);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getMonthLabels()));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);

        barChart.getDescription().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.invalidate();
    }

    private List<String> getMonthLabels() {
        List<String> monthLabels = new ArrayList<>();
        for (int i = 1; i <= monthlyExpenses.size(); i++) {
            monthLabels.add("Month " + i);
        }
        return monthLabels;
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

        Log.d("VoiceCommand", "Recognized command: " + command); // Log the recognized command
        if (command.contains("navigate") && command.contains("daily")) {
            redirectActivity(MonthlyActivity.this, DailyActivity.class);
        }else if (command.contains("navigate") && command.contains("profile")) {
            redirectActivity(MonthlyActivity.this, MainActivity.class);
        } else if (command.contains("navigate") && command.contains("monthly")) {
            redirectActivity(MonthlyActivity.this, MonthlyActivity.class);
        } else if (command.contains("navigate") && command.contains("yearly")) {
            redirectActivity(MonthlyActivity.this, YearlyActivity.class);
        }else if (command.contains("select month")) {
            // Extract the month from the spoken command
            String month = extractMonthFromCommand(command);

            if (month != null) {
                // Map month names to their index in the spinner
                HashMap<String, Integer> monthIndexMap = new HashMap<>();
                monthIndexMap.put("january", 0);
                monthIndexMap.put("february", 1);
                monthIndexMap.put("march", 2);
                monthIndexMap.put("april", 3);
                monthIndexMap.put("may", 4);
                monthIndexMap.put("june", 5);
                monthIndexMap.put("july", 6);
                monthIndexMap.put("august", 7);
                monthIndexMap.put("september", 8);
                monthIndexMap.put("october", 9);
                monthIndexMap.put("november", 10);
                monthIndexMap.put("december", 11);

                // Getting the index of the selected month
                Integer monthIndex = monthIndexMap.get(month);

                if (monthIndex != null) {
                    // Setting the selected month in the Spinner
                    Spinner monthSpinner = findViewById(R.id.monthSpinner);
                    monthSpinner.setSelection(monthIndex);
                } else {
                    // Handling case where month is not recognized
                    Toast.makeText(this, "Month not recognized", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handling case where month is not mentioned in the command
                Toast.makeText(this, "No month mentioned", Toast.LENGTH_SHORT).show();
            }
        }
        else if (command.contains("budget")) {
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
            // log out the user
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
            logoutUser(this);
        } else {
            Toast.makeText(this, "Command not recognized", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyCustomBackgroundDrawable(EditText editText) {
        // Create a gradient drawable with angular corners
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(getResources().getColor(R.color.light_grey));
        drawable.setCornerRadius(20); // Setting radius to control the degree of rounding

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
    // Method to extract the month from the spoken command
    private String extractMonthFromCommand(String command) {
        // List of month names
        String[] monthNames = {"january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december"};

        // Iterate through the month names and check if any is present in the command
        for (String month : monthNames) {
            if (command.contains(month)) {
                return month; // Return the matched month
            }
        }

        return null; // Return null if no month is found in the command
    }
    // Method to extract the budget amount from the spoken command
    private float addBudgetAmountFromCommand(String command) {
        // Split the command into words
        String[] words = command.split(" ");

        // Searching for the budget amount in the words
        for (int i = 0; i < words.length; i++) {
            if (words[i].equals("budget") && i < words.length - 1) {
                // Checking if the next word is a valid number representing the budget amount
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

    private void updateGraphAndPieChart(float expenseAmount) {
        displayPieChart();
        updateBarChart();
        calculateAndDisplayTotalExpense();
    }
    // Method to display the pie chart
    private void displayPieChart() {
        // to display the pie chart based on updated data
    }
    private void calculateAndDisplayTotalExpense() {
        // Calculating the total expense from the monthly expenses list
        float totalExpense = calculateTotalExpense();

        // Updating the TextView to display the total expense
        textViewTotalExpense.setText("Total Expense: " + totalExpense);
    }

}
