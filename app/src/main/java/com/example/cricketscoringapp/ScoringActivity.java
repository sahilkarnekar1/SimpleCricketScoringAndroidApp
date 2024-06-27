package com.example.cricketscoringapp;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.AdapterClasses.BallsAdapter;
import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.Models.Player;
import com.example.cricketscoringapp.Models.Team;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoringActivity extends AppCompatActivity {

    private String matchId;
    private DatabaseReference matchRef;
    private TextView strikerTextView, nonStrikerTextView, bowlerTextView, battingTeamTextView, currentScoreTextView,totalOversTextview,bowlingTeamNameTextview;
    private String currentStriker, currentNonStriker, currentBowler, battingTeam, bowlingTeam;
    private int totalRuns = 0, totalWickets = 0, totalBalls = 0, currentOver = 0;
    private String lastActionId; // To store the last action for undo functionality
    private ArrayList<String> bowlersArraylist;
    private ArrayList<String> batsmansArraylist;
    private Map<String, Integer> bowlerBalls; // To track the number of balls bowled by each bowler
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId;
    private DatabaseReference userRef;
    Button swappingButton;
    DatabaseReference totalOversstaticRef;
    double staticOversOfMatch;
    double dynamicOversOfMatch;
    double totalOvers;
    String testVariable = null;
    String extraType = null;
    boolean wicketGone = false;
    String wicketPlayerName;

    private RecyclerView recyclerView;
    private BallsAdapter adapter;
    private List<Map<String, Object>> ballsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);

        bowlersArraylist = new ArrayList<>();
        batsmansArraylist = new ArrayList<>();
        bowlerBalls = new HashMap<>(); // Initialize the bowler balls map

        strikerTextView = findViewById(R.id.strikerTextView);
        nonStrikerTextView = findViewById(R.id.nonStrikerTextView);
        bowlerTextView = findViewById(R.id.bowlerTextView);
        battingTeamTextView = findViewById(R.id.battingTeamTextView);
        currentScoreTextView = findViewById(R.id.currentScoreTextView);
        swappingButton = findViewById(R.id.swapButton);
        totalOversTextview = findViewById(R.id.totalOversTxt);
        bowlingTeamNameTextview = findViewById(R.id.bowlingTeamNamet);
        recyclerView = findViewById(R.id.bowlsRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        matchId = getIntent().getStringExtra("matchId");
        matchRef = FirebaseDatabase.getInstance().getReference("matches").child(matchId).child("scorecard").child("firstInning");

        totalOversstaticRef = FirebaseDatabase.getInstance().getReference("matches").child(matchId);

        userId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        loadMatchData();

        setButtonListeners();

        swappingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapStrikers();
            }
        });

        fetchDataFromFirebase();

        ballsList = new ArrayList<>();
        adapter = new BallsAdapter(ballsList);
        recyclerView.setAdapter(adapter);






    }

    private void fetchDataFromFirebase() {
        matchRef.child("overs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int n = (int) snapshot.getChildrenCount();
                String length = String.valueOf(n);

                ballsList.clear();
                        for(DataSnapshot newSnap : snapshot.child(length).child("balls").getChildren()){
                            Map<String, Object> map = (Map<String, Object>) newSnap.getValue();
                            ballsList.add(map);

                        }
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(ballsList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadMatchData() {
        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentStriker = snapshot.child("currentStriker").getValue(String.class);
                currentNonStriker = snapshot.child("currentNonStriker").getValue(String.class);
                currentBowler = snapshot.child("currentBowler").getValue(String.class);
                battingTeam = snapshot.child("battingTeam").getValue(String.class);
                bowlingTeam = snapshot.child("bowlingTeam").getValue(String.class);

                if (currentStriker != null && currentNonStriker != null && currentBowler != null && battingTeam != null && bowlingTeam != null) {
                    strikerTextView.setText("Striker: " + currentStriker);
                    nonStrikerTextView.setText("Non-Striker: " + currentNonStriker);
                    bowlerTextView.setText("Bowler: " + currentBowler);

                    userRef.child("teams").child(battingTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Team team = snapshot.getValue(Team.class);
                            String setBattingTeamName = team.getName();

                            battingTeamTextView.setText("Batting Team: " + setBattingTeamName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    userRef.child("teams").child(bowlingTeam).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Team team = snapshot.getValue(Team.class);
                            String setBowlingTeamName = team.getName();

                            bowlingTeamNameTextview.setText("Bowling Team: " + setBowlingTeamName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    Toast.makeText(ScoringActivity.this, "Failed to load match data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScoringActivity.this, "Failed to load match data", Toast.LENGTH_SHORT).show();
            }
        });

        totalOversstaticRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Match match = snapshot.getValue(Match.class);
                staticOversOfMatch = match.getOvers();
                totalOversTextview.setText("("+dynamicOversOfMatch +"/"+staticOversOfMatch+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void setButtonListeners() {
        findViewById(R.id.runButton0).setOnClickListener(v -> updateScore(0, false));
        findViewById(R.id.runButton1).setOnClickListener(v -> updateScore(1, false));
        findViewById(R.id.runButton2).setOnClickListener(v -> updateScore(2, false));
        findViewById(R.id.runButton3).setOnClickListener(v -> updateScore(3, false));
        findViewById(R.id.runButton4).setOnClickListener(v -> updateScore(4, false));
        findViewById(R.id.runButton5).setOnClickListener(v -> updateScore(5, false));
        findViewById(R.id.runButton6).setOnClickListener(v -> updateScore(6, false));

        findViewById(R.id.wideButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraType = "Wide";
                showExtraRunsDialog(true);
            }
        });

        findViewById(R.id.noBallButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extraType="No";
                showExtraRunsDialog(true);
            }
        });

        findViewById(R.id.wicketBallButton).setOnClickListener(v -> showWicketDialog());

    }

    private void showExtraRunsDialog(boolean isWide) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isWide ? "Wide Ball" : "No Ball");
        builder.setItems(new CharSequence[]{"0", "1", "2", "3", "4", "5", "6"}, (dialog, which) -> updateScore(which, isWide));
        builder.show();
    }

    private void showWicketDialog() {
        String[] wicketTypes = {"Caught", "Run Out", "Bowled", "LBW"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Wicket Type");
        builder.setItems(wicketTypes, (dialog, which) -> {
            switch (which) {
                case 0: // Caught
                case 2: // Bowled
                case 3: // LBW
                    handleWicketEvent(wicketTypes[which], currentStriker, 0,false);
                    break;
                case 1: // Run Out
                    showRunOutDialog();
                    break;
            }
        });
        builder.show();
    }

    private void showRunOutDialog() {
        String[] players = {currentStriker, currentNonStriker};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Who is Out?");
        builder.setItems(players, (dialog, which) -> {
            String playerOut = players[which];
            showRunOutRunsDialog(playerOut, which == 0 ? 0 : 1);
        });
        builder.show();
    }

    private void showRunOutRunsDialog(String playerOut, int playerPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Runs before Run Out");

        final CharSequence[] runOptions = {"0", "1", "2", "3", "4", "5", "6", "Wide", "No Ball"};
        builder.setItems(runOptions, (dialog, which) -> {
            String selectedOption = runOptions[which].toString();
            if (selectedOption.equals("Wide") || selectedOption.equals("No Ball")) {
                boolean isWide = selectedOption.equals("Wide");
                if (isWide){
                    testVariable = "Wide";
                }else {
                    testVariable = "No";
                }
                showExtraRunsDialogForRunOut(isWide, playerOut);
            } else {
                int runs = Integer.parseInt(selectedOption);
                handleWicketEvent("Run Out", playerOut, runs, false);
            }
        });

        builder.show();
    }

    private void showExtraRunsDialogForRunOut(boolean isWide, String playerOut) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isWide ? "Wide Ball" : "No Ball");
        builder.setItems(new CharSequence[]{"0", "1", "2", "3", "4", "5", "6"}, (dialog, which) -> {
            int runs = which; // This is the run value selected
            handleWicketEvent("Run Out", playerOut, runs, true); // Handle run out event with extras
        });
        builder.show();
    }

    private void handleWicketEvent(String type, String playerOut, int runs, boolean isExtra) {

        wicketGone = true;
        wicketPlayerName = playerOut;

        updateScore(runs, isExtra);  // Update the score with the runs before the wicket
        recordWicket(playerOut, type, runs);

        dynamicOversOfMatch = totalBalls / 6 + (totalBalls % 6) * 0.1;
        checkEndOfInning();
    }

    private void updateScore(int runs, boolean isExtra) {


        int totalRunsUpdate = runs;

        if (isExtra) {
            totalRunsUpdate += 1; // Extra ball adds one run

        } else {
            totalBalls++;
            // Update bowler's ball count
            int bowlerBallCount = bowlerBalls.getOrDefault(currentBowler, 0) + 1;
            bowlerBalls.put(currentBowler, bowlerBallCount);
        }

        totalRuns += totalRunsUpdate;

        DatabaseReference batsmenRef = matchRef.child("batsmen");
        DatabaseReference bowlerRef = matchRef.child("bowlers").child(currentBowler);

        batsmenRef.child(currentStriker).child("runs").setValue(ServerValue.increment(runs));

        if (!isExtra){
            batsmenRef.child(currentStriker).child("balls").setValue(ServerValue.increment(1));
        }


        bowlerRef.child("runsConceded").setValue(ServerValue.increment(totalRunsUpdate));
        if (!isExtra) {
            // Update the bowler's overs in the format of 0.1, 0.2, ..., 1.0, 1.1, ...
            int bowlerBallsCount = bowlerBalls.get(currentBowler);
            int overs = bowlerBallsCount / 6;
            int balls = bowlerBallsCount % 6;
            double oversFormat = overs + (balls / 10.0);
            bowlerRef.child("overs").setValue(oversFormat);
        }

        Map<String, Object> ballData = new HashMap<>();
        ballData.put("ballNumber", totalBalls % 6 == 0 ? 6 : totalBalls % 6); // Ball number within the over
        ballData.put("striker", currentStriker);
        ballData.put("runs", runs);
        ballData.put("isExtra", isExtra);
        if (extraType != null) {
            ballData.put("extraType", extraType); // Add extraType to ballData if it's an extra ball
            extraType = null;
        }

        if (testVariable != null){
            if (testVariable == "Wide"){
                ballData.put("extraType", "Wide");
            } else if (testVariable == "No") {
                ballData.put("extraType", "No");
            }

        }

        if (wicketGone == true){
            ballData.put("Wicket", wicketPlayerName);
        }


        // Get the reference to the current over
        DatabaseReference overRef = matchRef.child("overs").child(String.valueOf(currentOver + 1));
        DatabaseReference ballRef = overRef.child("balls").push();
        ballRef.setValue(ballData);
        lastActionId = ballRef.getKey();

        wicketGone = false;
        testVariable=null;

        matchRef.child("totalRuns").setValue(totalRuns);

// Update the display of total overs correctly
        int completedOvers = totalBalls / 6;
        int ballsInCurrentOver = totalBalls % 6;
        totalOvers = completedOvers + (ballsInCurrentOver / 10.0);
        matchRef.child("totalOvers").setValue(totalOvers);// Display overs in proper format
        currentScoreTextView.setText("Score: " + totalRuns + "/" + totalWickets);

        totalOversstaticRef.child("scorecard").child("firstInning").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dynamicOversOfMatch = snapshot.child("totalOvers").getValue(Double.class);
                totalOversTextview.setText("("+dynamicOversOfMatch +"/"+staticOversOfMatch+")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dynamicOversOfMatch = totalBalls / 6 + (totalBalls % 6) * 0.1;

        // Swap striker and non-striker if runs are odd
        if (runs % 2 != 0 && totalWickets != 10 && dynamicOversOfMatch != staticOversOfMatch) {
            swapStrikers();
        }

        // Check if the over is complete
        if (totalBalls % 6 == 0 && !isExtra && dynamicOversOfMatch != staticOversOfMatch && totalWickets != 10) {
            currentOver++;
            changeBowler();
            swapStrikers();
        }

        checkEndOfInning();

    }
    private void checkEndOfInning() {
        if (totalWickets == 10 || dynamicOversOfMatch >= staticOversOfMatch) {
            showEndOfInningDialog();
        }
    }

    private void showEndOfInningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Inning Ended");
        builder.setMessage("All 10 wickets are down or overs limit reached. Do you want to start the next inning?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startNextInning();
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    private void startNextInning() {
        String temp;
        temp = battingTeam;
        battingTeam = bowlingTeam;
        bowlingTeam = temp;


        Intent intent = new Intent(ScoringActivity.this, NextInningScoringActivity.class);
        intent.putExtra("matchId", matchId);
        intent.putExtra("newBattingTeam",battingTeam);
        intent.putExtra("newBowlingTeam",bowlingTeam);
        startActivity(intent);

        finish();
    }

    private void swapStrikers() {
        String temp = currentStriker;
        currentStriker = currentNonStriker;
        currentNonStriker = temp;

        matchRef.child("currentStriker").setValue(currentStriker);
        matchRef.child("currentNonStriker").setValue(currentNonStriker);

        strikerTextView.setText("Striker: " + currentStriker);
        nonStrikerTextView.setText("Non-Striker: " + currentNonStriker);
    }

    private void changeBowler() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select New Bowler");

        DatabaseReference bowlingTeamRef = userRef.child("teams").child(bowlingTeam).child("players");
        bowlingTeamRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bowlersArraylist.clear();
                for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                    Player player = playerSnapshot.getValue(Player.class);
                    if (player != null) {
                        bowlersArraylist.add(player.getName());
                    }
                }

                // Convert ArrayList to CharSequence[]
                CharSequence[] bowlersArray = bowlersArraylist.toArray(new CharSequence[0]);

                builder.setItems(bowlersArray, (dialog, which) -> {
                    currentBowler = bowlersArraylist.get(which);
                    matchRef.child("currentBowler").setValue(currentBowler);
                    bowlerTextView.setText("Bowler: " + currentBowler);

                    // Initialize bowler ball count if not present
                    if (!bowlerBalls.containsKey(currentBowler)) {
                        bowlerBalls.put(currentBowler, 0);
                    }
                });

                // Show the dialog after setting items
                builder.show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScoringActivity.this, "Failed to load bowlers", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void recordWicket(String playerOut, String type, int runs) {
        totalWickets++;
        matchRef.child("wickets").child(String.valueOf(totalWickets)).setValue(playerOut + " - " + type);
        matchRef.child("totalWickets").setValue(totalWickets);


        matchRef.child("bowlers").child(currentBowler).child("wickets").setValue(ServerValue.increment(1));


if (totalWickets != 10){
    if (playerOut.equals(currentStriker)) {
        currentStriker = null; // Set new striker later
        // Update UI and DB
        strikerTextView.setText("Striker: " + "New Batsman");
        matchRef.child("currentStriker").setValue("New Batsman");
    }
    else {
        currentNonStriker = null; // Set new non-striker later
        // Update UI and DB
        nonStrikerTextView.setText("Non-Striker: " + "New Batsman");
        matchRef.child("currentNonStriker").setValue("New Batsman");
    }

    // Load new batsman into the game (Assuming from a predefined list or input from the user)
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Select New Batsman");


    DatabaseReference batsmansTeamRef = userRef.child("teams").child(battingTeam).child("players");
    batsmansTeamRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            batsmansArraylist.clear();
            for (DataSnapshot playerSnapshot : snapshot.getChildren()) {
                Player player = playerSnapshot.getValue(Player.class);
                if (player != null) {
                    batsmansArraylist.add(player.getName());
                }
            }
            batsmansArraylist.add("Add New Player");
            // Convert ArrayList to CharSequence[]
            CharSequence[] batsmansArray = batsmansArraylist.toArray(new CharSequence[0]);

            builder.setItems(batsmansArray, (dialog, which) -> {
                String newBatsman = batsmansArray[which].toString();
                if (currentStriker == null) {
                    currentStriker = newBatsman;
                    strikerTextView.setText("Striker: " + currentStriker);
                    matchRef.child("currentStriker").setValue(currentStriker);
                } else {
                    currentNonStriker = newBatsman;
                    nonStrikerTextView.setText("Non-Striker: " + currentNonStriker);
                    matchRef.child("currentNonStriker").setValue(currentNonStriker);
                }
            });
            builder.show();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(ScoringActivity.this, "Failed to load batsmen", Toast.LENGTH_SHORT).show();
        }
    });
}

        currentScoreTextView.setText("Score: " + totalRuns + "/" + totalWickets);
    }



}
