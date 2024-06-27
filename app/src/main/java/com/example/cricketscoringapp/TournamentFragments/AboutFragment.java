package com.example.cricketscoringapp.TournamentFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cricketscoringapp.R;


public class AboutFragment extends Fragment {
    private TextView tournamentIdTextView;
    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        tournamentIdTextView = view.findViewById(R.id.textTournaId);

        if (getArguments() != null) {
            String tournamentId = getArguments().getString("TOURNAMENT_ID");
            tournamentIdTextView.setText("Tournament ID: " + tournamentId);
        }

        return view;
    }
}