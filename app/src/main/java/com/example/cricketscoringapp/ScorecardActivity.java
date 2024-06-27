package com.example.cricketscoringapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.AdapterClasses.BallsAdapter;
import com.example.cricketscoringapp.AdapterClasses.BatsmanAdapter;
import com.example.cricketscoringapp.AdapterClasses.BowlerAdapter;
import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.Models.Team;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScorecardActivity extends AppCompatActivity {

    TextView battingvsbowlingtextview, totalscoretextview, overstextview, battingteamtextview, bowlingteamtextview, strikertextview, nonstrikertextview, currentbowlertextview,targetTextview,requiredTextView;
    RecyclerView overRecview, batsmansRecview, bowlersRecview;

    DatabaseReference matchRef;
    String ownerUserId;
    String battingTeamId, bowlingTeamId, battingTeamName, bowlingTeamName;
    double totalMatchOvers;
    private BallsAdapter adapter;
    private List<Map<String, Object>> ballsList;

    private BatsmanAdapter batsmanAdapter;
    private List<Map<String, Object>> batsmanList;
    private BowlerAdapter bowlerAdapter;
    private List<Map<String, Object>> bowlerList;
    Button scorecardButton;
    int totalRunsTargetFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scorecard);

        String matchId = getIntent().getStringExtra("matchId");

        battingvsbowlingtextview = findViewById(R.id.batvsbowltextview);
        totalscoretextview = findViewById(R.id.scoretextviewmatches);
        overstextview = findViewById(R.id.totalOversScorecard);
        battingteamtextview = findViewById(R.id.battingTeaNameTextViewScorecard);
        bowlingteamtextview = findViewById(R.id.bowlingteamnametextviewscorecard);
        strikertextview = findViewById(R.id.strikertextviewscorecard);
        nonstrikertextview = findViewById(R.id.nonstrikertextviewscorecard);
        currentbowlertextview = findViewById(R.id.bowlertextviewscorecard);
        overRecview = findViewById(R.id.ballsScorecardRecyclerviewOver);
        batsmansRecview = findViewById(R.id.batsmansRecviewScorecard);
        bowlersRecview = findViewById(R.id.bowlersRecviewScorecard);
        scorecardButton = findViewById(R.id.scorecardButton);
        targetTextview=findViewById(R.id.targetTextviewScorecard);
        requiredTextView=findViewById(R.id.requiredTextviewScorecard);
        scorecardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScorecardActivity.this, TwoTeamsScorecardActivity.class);
                intent.putExtra("matchId",matchId);
                startActivity(intent);
            }
        });

        overRecview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ballsList = new ArrayList<>();
        adapter = new BallsAdapter(ballsList);
        overRecview.setAdapter(adapter);

        batsmansRecview.setLayoutManager(new LinearLayoutManager(this));
        batsmanList = new ArrayList<>();
        batsmanAdapter = new BatsmanAdapter(batsmanList);
        batsmansRecview.setAdapter(batsmanAdapter);

        bowlersRecview.setLayoutManager(new LinearLayoutManager(this));
        bowlerList = new ArrayList<>();
        bowlerAdapter = new BowlerAdapter(bowlerList);
        bowlersRecview.setAdapter(bowlerAdapter);

        matchRef = FirebaseDatabase.getInstance().getReference("matches").child(matchId);
        DatabaseReference firstInningRef = matchRef.child("scorecard").child("firstInning");
        DatabaseReference secondInningRef = matchRef.child("scorecard").child("secondInning");

        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Match match = snapshot.getValue(Match.class);
                if (match != null) {
                    ownerUserId = match.getUserId();
                    totalMatchOvers = match.getOvers();

                    secondInningRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                firstInningRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       int  totalRunsTarget = snapshot.child("totalRuns").getValue(Integer.class);
                                        totalRunsTargetFinal = totalRunsTarget+1;
                                        targetTextview.setText("Target : "+totalRunsTargetFinal);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                                loadInningData(snapshot, "secondInning");

                            } else {
                                firstInningRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            loadInningData(snapshot, "firstInning");
                                        } else {
                                            totalscoretextview.setText("Match has not started yet");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle error
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void loadInningData(DataSnapshot inningSnapshot, String inningType) {
        battingTeamId = inningSnapshot.child("battingTeam").getValue(String.class);
        bowlingTeamId = inningSnapshot.child("bowlingTeam").getValue(String.class);

        int totalRuns = inningSnapshot.child("totalRuns").getValue(Integer.class);
        int totalWickets = inningSnapshot.child("totalWickets").getValue(Integer.class);
        double totalOvers = inningSnapshot.child("totalOvers").getValue(Double.class);
        String currentBowler = inningSnapshot.child("currentBowler").getValue(String.class);
        String currentStriker = inningSnapshot.child("currentStriker").getValue(String.class);
        String currentNonStriker = inningSnapshot.child("currentNonStriker").getValue(String.class);

        totalscoretextview.setText(String.valueOf(totalRuns) + " / " + String.valueOf(totalWickets));
        overstextview.setText(String.valueOf(totalOvers) + " / " + String.valueOf(totalMatchOvers));
        strikertextview.setText(currentStriker);
        nonstrikertextview.setText(currentNonStriker);
        currentbowlertextview.setText(currentBowler);

        if (battingTeamId != null && bowlingTeamId != null) {
            FirebaseDatabase.getInstance().getReference("users").child(ownerUserId).child("teams").child(battingTeamId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Team team = snapshot.getValue(Team.class);
                    if (team != null) {
                        battingTeamName = team.getName();
                        updateTeamsTextView();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });

            FirebaseDatabase.getInstance().getReference("users").child(ownerUserId).child("teams").child(bowlingTeamId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Team team = snapshot.getValue(Team.class);
                    if (team != null) {
                        bowlingTeamName = team.getName();
                        updateTeamsTextView();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }

        ballsList.clear();
        for (DataSnapshot overSnapshot : inningSnapshot.child("overs").getChildren()) {
            for (DataSnapshot ballSnapshot : overSnapshot.child("balls").getChildren()) {
                Map<String, Object> ballData = (Map<String, Object>) ballSnapshot.getValue();
                ballsList.add(ballData);
            }
        }
        adapter.notifyDataSetChanged();
        overRecview.scrollToPosition(ballsList.size() - 1);

        batsmanList.clear();
        for (DataSnapshot batsmanSnap : inningSnapshot.child("batsmen").getChildren()) {
            Map<String, Object> batsmanData = (Map<String, Object>) batsmanSnap.getValue();
            Map<String, Object> batsmanWithKey = new HashMap<>(batsmanData);
            batsmanWithKey.put("name", batsmanSnap.getKey());
            batsmanList.add(batsmanWithKey);
        }
        batsmanAdapter.notifyDataSetChanged();

        bowlerList.clear();
        for (DataSnapshot bowlerSnap : inningSnapshot.child("bowlers").getChildren()) {
            Map<String, Object> bowlerData = (Map<String, Object>) bowlerSnap.getValue();
            Map<String, Object> bowlerWithKey = new HashMap<>(bowlerData);
            bowlerWithKey.put("name", bowlerSnap.getKey());
            bowlerList.add(bowlerWithKey);
        }
        bowlerAdapter.notifyDataSetChanged();


        matchRef.child("wonStatus").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String status = snapshot.getValue(String.class);
                    requiredTextView.setText(status);
                }else {
                    if (inningType == "secondInning"){
                        int requiredRuns = totalRunsTargetFinal - totalRuns;
                        int complete_overs = (int) totalOvers;
                        double fractional_part = totalOvers - complete_overs;
                        int  additional_balls = (int) (fractional_part * 10);
                        int  total_balls = (complete_overs * 6) + (additional_balls);

                        int remainingBalls = (int) ((totalMatchOvers * 6) - (total_balls));

                        requiredTextView.setText(battingTeamName +" Need "+requiredRuns+" in "+remainingBalls +" to win");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void updateTeamsTextView() {
        if (battingTeamName != null && bowlingTeamName != null) {
            battingvsbowlingtextview.setText(battingTeamName + " Vs " + bowlingTeamName);
            battingteamtextview.setText(battingTeamName);
            bowlingteamtextview.setText(bowlingTeamName);
        }
    }
}
