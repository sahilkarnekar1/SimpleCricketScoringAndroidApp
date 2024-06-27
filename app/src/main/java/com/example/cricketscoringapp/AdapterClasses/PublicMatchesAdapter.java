package com.example.cricketscoringapp.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.DisplayPublicMatcheOfTournamentActivity;
import com.example.cricketscoringapp.Models.Match;
import com.example.cricketscoringapp.Models.Team;
import com.example.cricketscoringapp.Models.Tournament;
import com.example.cricketscoringapp.R;
import com.example.cricketscoringapp.ScorecardActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.List;

public class PublicMatchesAdapter extends RecyclerView.Adapter<PublicMatchesAdapter.PublicViewHolder> {

    private List<Match> matches;

    private Context context;

    public PublicMatchesAdapter(List<Match> matches, Context context) {
        this.matches = matches;
        this.context = context;
    }

    @NonNull
    @Override
    public PublicMatchesAdapter.PublicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.matches_public_item, parent, false);
        return new PublicMatchesAdapter.PublicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicMatchesAdapter.PublicViewHolder holder, int position) {

        Match match = matches.get(position);

        String team1Id = match.getTeam1Id();
        String team2Id = match.getTeam2Id();

      DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(match.getUserId()).child("teams");

     userRef.child(team1Id).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             Team team = snapshot.getValue(Team.class);
             String team1Name = team.getName();
             holder.team1.setText(team1Name);
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });

     userRef.child(team2Id).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             Team team = snapshot.getValue(Team.class);
             String team2Name = team.getName();
             holder.team2.setText(team2Name);
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ScorecardActivity.class);
                intent.putExtra("matchId", match.getMatchId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class PublicViewHolder extends RecyclerView.ViewHolder {

        TextView team1,team2;

        public PublicViewHolder(@NonNull View itemView) {
            super(itemView);

            team1 = itemView.findViewById(R.id.matches_team1_textView);
            team2 = itemView.findViewById(R.id.matches_team2_textView2);
        }
    }
}
