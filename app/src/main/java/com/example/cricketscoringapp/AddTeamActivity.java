package com.example.cricketscoringapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cricketscoringapp.Models.Team;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTeamActivity extends AppCompatActivity {
    private EditText teamNameEditText;
    private Button addTeamButton;

    private DatabaseReference databaseTeams;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_team);

        teamNameEditText = findViewById(R.id.teamNameEditText);
        addTeamButton = findViewById(R.id.addTeamButton);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseTeams = FirebaseDatabase.getInstance().getReference("users").child(userId).child("teams");

            addTeamButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addTeam();
                }
            });
        }
    }

    private void addTeam() {
        String teamName = teamNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(teamName)) {
            Toast.makeText(this, "Please enter a team name", Toast.LENGTH_SHORT).show();
            return;
        }

        String teamId = databaseTeams.push().getKey();
        Team team = new Team(teamId, teamName);

        assert teamId != null;
        databaseTeams.child(teamId).setValue(team).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddTeamActivity.this, "Team added", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity and go back to the previous one
            } else {
                Toast.makeText(AddTeamActivity.this, "Failed to add team", Toast.LENGTH_SHORT).show();
            }
        });
    }
}