package com.example.cricketscoringapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.AdapterClasses.PublicMatchesAdapter;
import com.example.cricketscoringapp.AdapterClasses.TournamentAdapterForMain;
import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.Models.Tournament;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DisplayPublicMatcheOfTournamentActivity extends AppCompatActivity {

    RecyclerView displayMatchesRecview;

    private PublicMatchesAdapter matchesAdapter;
    private List<Match> matchList;

    private DatabaseReference databaseMatches;

    String tournamentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_display_public_matche_of_tournament);

        tournamentId = getIntent().getStringExtra("TOURNAMENT_ID");

        displayMatchesRecview = findViewById(R.id.displayMatchesForPublicRecview);
        displayMatchesRecview.setLayoutManager(new LinearLayoutManager(DisplayPublicMatcheOfTournamentActivity.this));
        matchList = new ArrayList<>();
        matchesAdapter = new PublicMatchesAdapter(matchList, DisplayPublicMatcheOfTournamentActivity.this); // Pass context here
        displayMatchesRecview.setAdapter(matchesAdapter);


        databaseMatches = FirebaseDatabase.getInstance().getReference("matches");

        databaseMatches.orderByChild("tournamentId").equalTo(tournamentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchList.clear();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Match match = postSnapshot.getValue(Match.class);
                    matchList.add(match);
                }
                matchesAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}