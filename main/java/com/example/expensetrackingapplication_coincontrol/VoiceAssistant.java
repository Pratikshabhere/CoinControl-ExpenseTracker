package com.example.expensetrackingapplication_coincontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;

public class VoiceAssistant {

    private static final int REQUEST_SPEECH_RECOGNITION = 1;

    private static VoiceAssistant instance;

    private VoiceAssistant() {
        // Private constructor to prevent instantiation
    }

    public static VoiceAssistant getInstance() {
        if (instance == null) {
            instance = new VoiceAssistant();
        }
        return instance;
    }

    public void startSpeechRecognition(Activity activity) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        activity.startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION);
    }

    public void processVoiceCommand(String command, Context context) {
        // Implementing logic to perform actions based on the voice command
        if (command.contains("navigate")) {
            // Implementing logic to navigate to a specific screen/activity
            Toast.makeText(context, "Navigating...", Toast.LENGTH_SHORT).show();
        } else if (command.contains("add budget")) {
            // Implementing logic to add budget
            Toast.makeText(context, "Adding budget...", Toast.LENGTH_SHORT).show();
        } else if (command.contains("calculate budget")) {
            // Implementing logic to calculate budget
            Toast.makeText(context, "Calculating budget...", Toast.LENGTH_SHORT).show();
        } else {
            // Handling unrecognized commands
            Toast.makeText(context, "Command not recognized", Toast.LENGTH_SHORT).show();
        }
    }
}
