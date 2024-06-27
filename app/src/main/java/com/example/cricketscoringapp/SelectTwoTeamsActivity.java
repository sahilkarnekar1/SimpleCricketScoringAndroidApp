package com.example.cricketscoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.SampleFiles.SelectTeamDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SelectTwoTeamsActivity extends AppCompatActivity {

    private Button selectTeam1Button, selectTeam2Button, letsPlayButton;
    private TextView team1NameTextView, team2NameTextView;
    private EditText oversEditText;
    private RadioGroup tossWinnerGroup, tossDecisionGroup;
    private RadioButton team1TossWinnerRadioButton, team2TossWinnerRadioButton, batRadioButton, bowlRadioButton;

    private String team1Id, team2Id, team1Name, team2Name,TOURNAMENT_ID;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseMatches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_two_teams);

        selectTeam1Button = findViewById(R.id.selectTeam1Button);
        selectTeam2Button = findViewById(R.id.selectTeam2Button);
        letsPlayButton = findViewById(R.id.letsPlayButton);
        team1NameTextView = findViewById(R.id.team1NameTextView);
        team2NameTextView = findViewById(R.id.team2NameTextView);
        oversEditText = findViewById(R.id.oversEditText);
        tossWinnerGroup = findViewById(R.id.tossWinnerGroup);
        tossDecisionGroup = findViewById(R.id.tossDecisionGroup);
        team1TossWinnerRadioButton = findViewById(R.id.team1TossWinnerRadioButton);
        team2TossWinnerRadioButton = findViewById(R.id.team2TossWinnerRadioButton);
        batRadioButton = findViewById(R.id.batRadioButton);
        bowlRadioButton = findViewById(R.id.bowlRadioButton);

        TOURNAMENT_ID = getIntent().getStringExtra("TOURNAMENT_ID");
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        databaseMatches = FirebaseDatabase.getInstance().getReference("matches");

        selectTeam1Button.setOnClickListener(v -> openSelectTeamDialog(1));
        selectTeam2Button.setOnClickListener(v -> openSelectTeamDialog(2));

        letsPlayButton.setOnClickListener(v -> createMatch());
    }

    private void openSelectTeamDialog(int teamNumber) {
        SelectTeamDialog dialog = new SelectTeamDialog(this, team -> {
            if (teamNumber == 1) {
                team1Id = team.getId();
                team1Name = team.getName();
                team1NameTextView.setText(team1Name);
            } else if (teamNumber == 2) {
                team2Id = team.getId();
                team2Name = team.getName();
                team2NameTextView.setText(team2Name);
            }
        });
        dialog.show();
    }

    private void createMatch() {
        if (team1Id == null || team2Id == null || team1Id.equals(team2Id)) {
            Toast.makeText(this, "Please select two different teams", Toast.LENGTH_SHORT).show();
            return;
        }

        String overs = oversEditText.getText().toString().trim();
        if (overs.isEmpty()) {
            Toast.makeText(this, "Please enter the number of overs", Toast.LENGTH_SHORT).show();
            return;
        }

        String tossWinner;
        if (team1TossWinnerRadioButton.isChecked()) {
            tossWinner = team1Id;
        } else if (team2TossWinnerRadioButton.isChecked()) {
            tossWinner = team2Id;
        } else {
            Toast.makeText(this, "Please select the toss winner", Toast.LENGTH_SHORT).show();
            return;
        }

        String electedTo;
        if (batRadioButton.isChecked()) {
            electedTo = "bat";
        } else if (bowlRadioButton.isChecked()) {
            electedTo = "bowl";
        } else {
            Toast.makeText(this, "Please select the toss decision", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String matchId = databaseMatches.push().getKey();

        Match match = new Match(matchId, userId, team1Id, team2Id, Double.parseDouble(overs), tossWinner, electedTo,TOURNAMENT_ID);
        databaseMatches.child(matchId).setValue(match);

        Intent intent = new Intent(SelectTwoTeamsActivity.this, StartInningsActivity.class);
        intent.putExtra("matchId", matchId);
        startActivity(intent);

        Toast.makeText(this, "Match created successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}
