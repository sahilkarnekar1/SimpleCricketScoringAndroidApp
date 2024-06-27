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

public class NextInningScoringActivity extends AppCompatActivity {

    TextView targetTxtView, requiredTxtView, battingTeamTxtView, bowlingTeamTxtView;
    Spinner strikerSpinner, nonStrikerSpinner, bowlerSpinner;
    Button btnStartScoring;
    String matchId, battingTeamId, bowlingTeamId;
    private DatabaseReference databaseMatches;
    private DatabaseReference databaseTeams;
    private ArrayList<String> battingTeamPlayers;
    private ArrayList<String> bowlingTeamPlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_next_inning_scoring);

        targetTxtView = findViewById(R.id.targetTexviewstartscoring2ndInningactivity);
        requiredTxtView = findViewById(R.id.requiredTextviewStartscoringseccindinning);
        battingTeamTxtView = findViewById(R.id.battingTeamNameTextViewstart2ndinningTextview);
        bowlingTeamTxtView = findViewById(R.id.bowlingTeamNameTextViewstart2ndinningTextview);
        strikerSpinner = findViewById(R.id.strikerSpinnerstart2ndinningTextview);
        nonStrikerSpinner = findViewById(R.id.nonStrikerSpinnerstart2ndinningTextview);
        bowlerSpinner = findViewById(R.id.bowlerSpinnerstart2ndinningTextview);
        btnStartScoring = findViewById(R.id.startScoringButtonstart2ndinningTextview);

        matchId = getIntent().getStringExtra("matchId");
        battingTeamId = getIntent().getStringExtra("newBattingTeam");
        bowlingTeamId = getIntent().getStringExtra("newBowlingTeam");

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
                    Intent intent = new Intent(NextInningScoringActivity.this, AddTeamPlayersActivity.class);
                    intent.putExtra("teamId", battingTeamId);
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
                    Intent intent = new Intent(NextInningScoringActivity.this, AddTeamPlayersActivity.class);
                    intent.putExtra("teamId", battingTeamId);
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
                    Intent intent = new Intent(NextInningScoringActivity.this, AddTeamPlayersActivity.class);
                    intent.putExtra("teamId", bowlingTeamId);
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

             int totalRunsFromDb =    snapshot.child("scorecard").child("firstInning").child("totalRuns").getValue(Integer.class);
int totalTarget = totalRunsFromDb +1;

targetTxtView.setText(String.valueOf(totalTarget));
                if (match != null) {
                 double totalOverFromDb = match.getOvers();
                  int totalBalls = (int) (totalOverFromDb * 6);
requiredTxtView.setText(battingTeamId +"Required"+String.valueOf(totalTarget)+" Runs in"+String.valueOf(totalBalls));


                    loadTeamData(battingTeamId, true);
                    loadTeamData(bowlingTeamId, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(NextInningScoringActivity.this, "Failed to load match data", Toast.LENGTH_SHORT).show();
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
                        battingTeamTxtView.setText(team.getName());
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
                        bowlingTeamTxtView.setText(team.getName());
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
                Toast.makeText(NextInningScoringActivity.this, "Failed to load team data", Toast.LENGTH_SHORT).show();
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
        DatabaseReference matchRef = databaseMatches.child(matchId).child("scorecard").child("secondInning");

        // Set initial match details
        matchRef.child("currentStriker").setValue(striker);
        matchRef.child("currentNonStriker").setValue(nonStriker);
        matchRef.child("currentBowler").setValue(bowler);
        matchRef.child("battingTeam").setValue(battingTeamId);
        matchRef.child("bowlingTeam").setValue(bowlingTeamId);
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

        Intent intent = new Intent(NextInningScoringActivity.this, StartSecondInningScoringActivity.class);
        intent.putExtra("matchId", matchId);
        startActivity(intent);
        finish();
    }

}