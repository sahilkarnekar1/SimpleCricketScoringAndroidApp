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

import com.example.cricketscoringapp.AdapterClasses.TeamAdapter;
import com.example.cricketscoringapp.AddTeamActivity;
import com.example.cricketscoringapp.Models.Team;
import com.example.cricketscoringapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TeamsFragment extends Fragment {
    private RecyclerView teamsRecyclerView;
    private TeamAdapter teamAdapter;
    private List<Team> teamList;
    private FloatingActionButton addTeamButton;

    private DatabaseReference databaseTeams;
    private FirebaseAuth mAuth;
    public TeamsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teams2, container, false);

        teamsRecyclerView = view.findViewById(R.id.teamsRecyclerView);
        teamsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        teamList = new ArrayList<>();
        teamAdapter = new TeamAdapter(this.getContext(),teamList);
        teamsRecyclerView.setAdapter(teamAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseTeams = FirebaseDatabase.getInstance().getReference("users").child(userId).child("teams");

            databaseTeams.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    teamList.clear();
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Team team = postSnapshot.getValue(Team.class);
                        teamList.add(team);
                    }
                    teamAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        addTeamButton = view.findViewById(R.id.addTeamButton);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTeamActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}