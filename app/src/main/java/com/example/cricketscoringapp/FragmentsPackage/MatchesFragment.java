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

import com.example.cricketscoringapp.AdapterClasses.TournamentAdapter;
import com.example.cricketscoringapp.AdapterClasses.TournamentAdapterForMain;
import com.example.cricketscoringapp.Models.Tournament;
import com.example.cricketscoringapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchesFragment extends Fragment {

    private RecyclerView tournamentsRecyclerView;
    private TournamentAdapterForMain tournamentAdapter;
    private List<Tournament> tournamentList;

    private DatabaseReference databaseTournaments;


    public MatchesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_matches, container, false);


        tournamentsRecyclerView = view.findViewById(R.id.TournamentsRecViewInMatchesFragment);
        tournamentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        tournamentList = new ArrayList<>();
        tournamentAdapter = new TournamentAdapterForMain(tournamentList, getContext()); // Pass context here
        tournamentsRecyclerView.setAdapter(tournamentAdapter);


databaseTournaments = FirebaseDatabase.getInstance().getReference("tournaments");

databaseTournaments.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        tournamentList.clear();
        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
            Tournament tournament = postSnapshot.getValue(Tournament.class);
            tournamentList.add(tournament);
        }
        tournamentAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});




        return  view;
    }
}