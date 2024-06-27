package com.example.cricketscoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.Models.Player;
import com.example.cricketscoringapp.Models.Team;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StartInningsActivity extends AppCompatActivity {

    TextView batTeamName, bowlTeamName;
    Spinner strikerSpinner, nonStrikerSpinner, bowlerSpinner;
    Button btnStartScoring;
    private String matchId;
    private DatabaseReference databaseMatches;
    private DatabaseReference databaseTeams;
    private String team1Id, team2Id, tosswinnerId, elected;
    private String battingTeam, bowlingTeam;
    private ArrayList<String> battingTeamPlayers;
    private ArrayList<String> bowlingTeamPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_innings);

        batTeamName = findViewById(R.id.battingTeamNameTextView);
        bowlTeamName = findViewById(R.id.bowlingTeamNameTextView);
        strikerSpinner = findViewById(R.id.strikerSpinner);
        nonStrikerSpinner = findViewById(R.id.nonStrikerSpinner);
        bowlerSpinner = findViewById(R.id.bowlerSpinner);
        btnStartScoring = findViewById(R.id.startScoringButton);

        matchId = getIntent().getStringExtra("matchId");
        databaseMatches = FirebaseDatabase.getInstance().getReference("matches");
        databaseTeams = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("teams");

        battingTeamPlayers = new ArrayList<>();
        bowlingTeamPlayers = new ArrayList<>();

        loadMatchData();

        btnStartScoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScoring();
            }
        });

        setupSpinnerListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMatchData();
    }

    private void setupSpinnerListeners() {
        strikerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlayer = battingTeamPlayers.get(position);
                if ("Add New Player".equals(selectedPlayer)) {
                    Intent intent = new Intent(StartInningsActivity.this, AddTeamPlayersActivity.class);
                    intent.putExtra("teamId", battingTeam);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        nonStrikerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlayer = battingTeamPlayers.get(position);
                if ("Add New Player".equals(selectedPlayer)) {
                    Intent intent = new Intent(StartInningsActivity.this, AddTeamPlayersActivity.class);
                    intent.putExtra("teamId", battingTeam);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        bowlerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPlayer = bowlingTeamPlayers.get(position);
                if ("Add New Player".equals(selectedPlayer)) {
                    Intent intent = new Intent(StartInningsActivity.this, AddTeamPlayersActivity.class);
                    intent.putExtra("teamId", bowlingTeam);
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadMatchData() {
        databaseMatches.child(matchId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Match match = snapshot.getValue(Match.class);
                if (match != null) {
                    team1Id = match.getTeam1Id();
                    team2Id = match.getTeam2Id();
                    tosswinnerId = match.getTossWinner();
                    elected = match.getElectedTo();

                    if (tosswinnerId.equals(team1Id) && elected.equals("bowl")) {
                        bowlingTeam = team1Id;
                        battingTeam = team2Id;
                    } else if (tosswinnerId.equals(team1Id) && elected.equals("bat")) {
                        battingTeam = team1Id;
                        bowlingTeam = team2Id;
                    } else if (tosswinnerId.equals(team2Id) && elected.equals("bowl")) {
                        bowlingTeam = team2Id;
                        battingTeam = team1Id;
                    } else if (tosswinnerId.equals(team2Id) && elected.equals("bat")) {
                        battingTeam = team2Id;
                        bowlingTeam = team1Id;
                    }

                    loadTeamData(battingTeam, true);
                    loadTeamData(bowlingTeam, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StartInningsActivity.this, "Failed to load match data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeamData(String teamId, final boolean isBattingTeam) {
        databaseTeams.child(teamId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Team team = snapshot.getValue(Team.class);
                if (team != null) {
                    if (isBattingTeam) {
                        batTeamName.setText(team.getName());
                        battingTeamPlayers.clear();
                        for (DataSnapshot playerSnapshot : snapshot.child("players").getChildren()) {
                            Player player = playerSnapshot.getValue(Player.class);
                            if (player != null) {
                                battingTeamPlayers.add(player.getName());
                            }
                        }
                        battingTeamPlayers.add("Add New Player");
                        updateSpinner(strikerSpinner, battingTeamPlayers);
                        updateSpinner(nonStrikerSpinner, battingTeamPlayers);

                    } else {
                        bowlTeamName.setText(team.getName());
                        bowlingTeamPlayers.clear();
                        for (DataSnapshot playerSnapshot : snapshot.child("players").getChildren()) {
                            Player player = playerSnapshot.getValue(Player.class);
                            if (player != null) {
                                bowlingTeamPlayers.add(player.getName());
                            }
                        }
                        bowlingTeamPlayers.add("Add New Player");
                        updateSpinner(bowlerSpinner, bowlingTeamPlayers);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StartInningsActivity.this, "Failed to load team data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSpinner(Spinner spinner, ArrayList<String> players) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, players);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void startScoring() {
        String striker = strikerSpinner.getSelectedItem().toString();
        String nonStriker = nonStrikerSpinner.getSelectedItem().toString();
        String bowler = bowlerSpinner.getSelectedItem().toString();

        if (striker.equals("Add New Player") || nonStriker.equals("Add New Player") || bowler.equals("Add New Player")) {
            Toast.makeText(this, "Please select valid players", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store the initial match data in the database under the scorecard node
        DatabaseReference matchRef = databaseMatches.child(matchId).child("scorecard").child("firstInning");

        // Set initial match details
        matchRef.child("currentStriker").setValue(striker);
        matchRef.child("currentNonStriker").setValue(nonStriker);
        matchRef.child("currentBowler").setValue(bowler);
        matchRef.child("battingTeam").setValue(battingTeam);
        matchRef.child("bowlingTeam").setValue(bowlingTeam);
        matchRef.child("totalRuns").setValue(0);
        matchRef.child("totalWickets").setValue(0);
        matchRef.child("totalOvers").setValue(0);

        // Initialize batsman data
        DatabaseReference batsmenRef = matchRef.child("batsmen");
        DatabaseReference strikerRef = batsmenRef.child(striker);
        strikerRef.child("runs").setValue(0);
        strikerRef.child("balls").setValue(0);

        DatabaseReference nonStrikerRef = batsmenRef.child(nonStriker);
        nonStrikerRef.child("runs").setValue(0);
        nonStrikerRef.child("balls").setValue(0);

        // Initialize bowler data
        DatabaseReference bowlersRef = matchRef.child("bowlers");
        DatabaseReference bowlerRef = bowlersRef.child(bowler);
        bowlerRef.child("overs").setValue(0);
        bowlerRef.child("runsConceded").setValue(0);
        bowlerRef.child("wickets").setValue(0);

        // Initialize overs data
        matchRef.child("overs").setValue(new ArrayList<>());

        Intent intent = new Intent(StartInningsActivity.this, ScoringActivity.class);
        intent.putExtra("matchId", matchId);
        startActivity(intent);
        finish();
    }



}