package com.example.expensetrackingapplication_coincontrol;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Utils {
    public static String getCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        return dateFormat.format(calendar.getTime());
    }
}
