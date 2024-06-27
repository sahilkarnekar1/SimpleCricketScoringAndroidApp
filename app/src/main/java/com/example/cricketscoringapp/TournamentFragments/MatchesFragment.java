package com.example.cricketscoringapp.TournamentFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cricketscoringapp.AdapterClasses.MatchesAdapter;
import com.example.cricketscoringapp.AdapterClasses.PublicMatchesAdapter;
import com.example.cricketscoringapp.DisplayPublicMatcheOfTournamentActivity;
import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.R;
import com.example.cricketscoringapp.SelectTwoTeamsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends Fragment {

    private Button startMatchButton;
    private RecyclerView matchesRecyclerView;
    private MatchesAdapter matchesAdapter;
    private List<Match> matchList;
    private DatabaseReference matchesRef;
    String tournamentId;

    public MatchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        tournamentId = getArguments().getString("TOURNAMENT_ID");
        View view = inflater.inflate(R.layout.fragment_matches2, container, false);

        startMatchButton = view.findViewById(R.id.startMatchButton);
        matchesRecyclerView = view.findViewById(R.id.matchesRecyclerView);

        matchesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        matchList = new ArrayList<>();
        matchesAdapter = new MatchesAdapter(matchList, requireContext());
        matchesRecyclerView.setAdapter(matchesAdapter);

        matchesRef = FirebaseDatabase.getInstance().getReference("matches");

        startMatchButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SelectTwoTeamsActivity.class);
            intent.putExtra("TOURNAMENT_ID",tournamentId);
            startActivity(intent);
        });




        loadMatches();



        return view;
    }
    private void loadMatches() {
        matchesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchList.clear();
                for (DataSnapshot matchSnapshot : snapshot.getChildren()) {
                    Match match = matchSnapshot.getValue(Match.class);
                    if (match != null && tournamentId.equals(match.getTournamentId())) {
                        matchList.add(match);
                    }
                }
                matchesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }
}