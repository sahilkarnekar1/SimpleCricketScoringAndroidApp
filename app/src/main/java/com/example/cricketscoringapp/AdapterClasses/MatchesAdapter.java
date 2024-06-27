package com.example.cricketscoringapp.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.Models.Team;
import com.example.cricketscoringapp.R;
import com.example.cricketscoringapp.ScoringActivity;
import com.example.cricketscoringapp.TournamentFragments.MatchesFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchViewHolder> {

    private List<Match> matchList;
    private Context context;

    public MatchesAdapter(List<Match> matchList, Context context) {
        this.matchList = matchList;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_public_item, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);

        String uId = match.getUserId();

        FirebaseDatabase.getInstance().getReference("users").child(uId).child("teams").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                String team1Name = snapshot.child(match.getTeam1Id()).child("name").getValue(String.class);

                String team2Name = snapshot.child(match.getTeam2Id()).child("name").getValue(String.class);

                holder.team1TextView.setText(team1Name);
                holder.team2TextView.setText(team2Name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView team1TextView, team2TextView, dateTextView, timeTextView;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            team1TextView = itemView.findViewById(R.id.matches_team1_textView);
            team2TextView = itemView.findViewById(R.id.matches_team2_textView2);

        }
    }
}
