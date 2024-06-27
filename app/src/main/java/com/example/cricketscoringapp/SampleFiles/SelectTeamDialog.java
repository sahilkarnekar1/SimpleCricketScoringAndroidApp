package com.example.cricketscoringapp.SampleFiles;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cricketscoringapp.Models.Team;
import com.example.cricketscoringapp.R;
import com.example.cricketscoringapp.SampleAdapters.TeamAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectTeamDialog extends Dialog {

    private ListView teamListView;
    private Button cancelButton;
    private ArrayList<Team> teamList;
    private TeamAdapter teamAdapter;
    private DatabaseReference databaseTeams;
    private FirebaseAuth mAuth;
    private OnTeamSelectedListener listener;

    public interface OnTeamSelectedListener {
        void onTeamSelected(Team team);
    }

    public SelectTeamDialog(@NonNull Context context, OnTeamSelectedListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_team);

        teamListView = findViewById(R.id.teamListView);
        cancelButton = findViewById(R.id.cancelButton);
        teamList = new ArrayList<>();
        teamAdapter = new TeamAdapter(getContext(), teamList);
        teamListView.setAdapter(teamAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseTeams = FirebaseDatabase.getInstance().getReference("users").child(userId).child("teams");

            databaseTeams.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    teamList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Team team = postSnapshot.getValue(Team.class);
                        teamList.add(team);
                    }
                    teamAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to load teams", Toast.LENGTH_SHORT).show();
                }
            });
        }

        teamListView.setOnItemClickListener((parent, view, position, id) -> {
            Team selectedTeam = teamList.get(position);
            listener.onTeamSelected(selectedTeam);
            dismiss();
        });

        cancelButton.setOnClickListener(v -> dismiss());
    }
}