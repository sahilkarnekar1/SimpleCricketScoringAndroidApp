package com.example.cricketscoringapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cricketscoringapp.Models.Tournament;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegisterTournamentActivity extends AppCompatActivity {
    private EditText tournamentNameEditText, locationEditText, startDateEditText, endDateEditText;
    private Button registerButton;
    private DatabaseReference databaseTournaments;
    private FirebaseAuth mAuth;

    private Calendar startDateCalendar = Calendar.getInstance();
    private Calendar endDateCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_tournament);

        tournamentNameEditText = findViewById(R.id.tournamentNameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        startDateEditText = findViewById(R.id.startDateEditText);
        endDateEditText = findViewById(R.id.endDateEditText);
        registerButton = findViewById(R.id.registerButton);

        // Get reference to 'tournaments' node in Firebase Realtime Database
        databaseTournaments = FirebaseDatabase.getInstance().getReference("tournaments");
        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerTournament();
            }
        });
    }

    public void showStartDatePickerDialog(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        startDateCalendar.set(Calendar.YEAR, year);
                        startDateCalendar.set(Calendar.MONTH, month);
                        startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(startDateEditText, startDateCalendar);
                    }
                },
                now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    public void showEndDatePickerDialog(View v) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        endDateCalendar.set(Calendar.YEAR, year);
                        endDateCalendar.set(Calendar.MONTH, month);
                        endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel(endDateEditText, endDateCalendar);
                    }
                },
                startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH), startDateCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(startDateCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateLabel(EditText editText, Calendar calendar) {
        String format = "yyyy-MM-dd";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
        editText.setText(sdf.format(calendar.getTime()));
    }

    private void registerTournament() {
        String name = tournamentNameEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(location) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(endDate)) {
            Toast.makeText(RegisterTournamentActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Generate a unique ID for the tournament
            String id = databaseTournaments.push().getKey();

            // Create a new Tournament object
            Tournament tournament = new Tournament(id, name, location, startDate, endDate, userId);

            // Save the tournament to the database
            databaseTournaments.child(id).setValue(tournament).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterTournamentActivity.this, "Tournament registered successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity and go back to previous activity
                } else {
                    Toast.makeText(RegisterTournamentActivity.this, "Failed to register tournament", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegisterTournamentActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}