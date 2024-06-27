package com.example.cricketscoringapp.FragmentsPackage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cricketscoringapp.AdapterClasses.BatsmanAdapter;
import com.example.cricketscoringapp.AdapterClasses.BowlerAdapter;
import com.example.cricketscoringapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstTeamScorecardFragment extends Fragment {

    private static final String ARG_MATCH_ID = "matchId";
    private String matchId;

    private DatabaseReference matchRef;
    private RecyclerView battingTeamRecview, bowlingTeamRecview;
    private BatsmanAdapter batsmanAdapter;
    private List<Map<String, Object>> batsmanList;
    private BowlerAdapter bowlerAdapter;
    private List<Map<String, Object>> bowlerList;

    public static FirstTeamScorecardFragment newInstance(String matchId) {
        FirstTeamScorecardFragment fragment = new FirstTeamScorecardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MATCH_ID, matchId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            matchId = getArguments().getString(ARG_MATCH_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_team_scorecard, container, false);

        battingTeamRecview = view.findViewById(R.id.batsmansrecyclerviewfinalscorecardteam1);
        bowlingTeamRecview = view.findViewById(R.id.bowlersrecyclerviewfinalscorecardteam1);

        battingTeamRecview.setLayoutManager(new LinearLayoutManager(getContext()));
        batsmanList = new ArrayList<>();
        batsmanAdapter = new BatsmanAdapter(batsmanList);
        battingTeamRecview.setAdapter(batsmanAdapter);

        bowlingTeamRecview.setLayoutManager(new LinearLayoutManager(getContext()));
        bowlerList = new ArrayList<>();
        bowlerAdapter = new BowlerAdapter(bowlerList);
        bowlingTeamRecview.setAdapter(bowlerAdapter);

        matchRef = FirebaseDatabase.getInstance().getReference("matches").child(matchId).child("scorecard").child("firstInning");
        loadInningData();

        return view;
    }

    private void loadInningData() {
        matchRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot inningSnapshot) {
                // Fetch and display batsman data
                batsmanList.clear();
                for (DataSnapshot batsmanSnap : inningSnapshot.child("batsmen").getChildren()) {
                    Map<String, Object> batsmanData = (Map<String, Object>) batsmanSnap.getValue();
                    Map<String, Object> batsmanWithKey = new HashMap<>(batsmanData);
                    batsmanWithKey.put("name", batsmanSnap.getKey());
                    batsmanList.add(batsmanWithKey);
                }
                batsmanAdapter.notifyDataSetChanged();

                // Fetch and display bowler data
                bowlerList.clear();
                for (DataSnapshot bowlerSnap : inningSnapshot.child("bowlers").getChildren()) {
                    Map<String, Object> bowlerData = (Map<String, Object>) bowlerSnap.getValue();
                    Map<String, Object> bowlerWithKey = new HashMap<>(bowlerData);
                    bowlerWithKey.put("name", bowlerSnap.getKey());
                    bowlerList.add(bowlerWithKey);
                }
                bowlerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}