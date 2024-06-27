package com.example.cricketscoringapp.FragmentsPackage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.cricketscoringapp.AdapterClasses.TournamentAdapter;
import com.example.cricketscoringapp.Models.Tournament;
import com.example.cricketscoringapp.R;
import com.example.cricketscoringapp.RegisterTournamentActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TournamentsFragment extends Fragment {

    private Button registerTournamentButton;
    private RecyclerView tournamentsRecyclerView;
    private TournamentAdapter tournamentAdapter;
    private List<Tournament> tournamentList;

    private DatabaseReference databaseTournaments;
    private FirebaseAuth mAuth;


    public TournamentsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tournaments, container, false);

        registerTournamentButton = view.findViewById(R.id.registerTournamentButton);
        tournamentsRecyclerView = view.findViewById(R.id.tournamentsRecyclerView);
        tournamentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tournamentList = new ArrayList<>();
        tournamentAdapter = new TournamentAdapter(tournamentList, getContext()); // Pass context here
        tournamentsRecyclerView.setAdapter(tournamentAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseTournaments = FirebaseDatabase.getInstance().getReference("tournaments");

            databaseTournaments.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    tournamentList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Tournament tournament = postSnapshot.getValue(Tournament.class);
                        tournamentList.add(tournament);
                    }
                    tournamentAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Failed to load tournaments", Toast.LENGTH_SHORT).show();
                }
            });
        }

        registerTournamentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterTournamentActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}