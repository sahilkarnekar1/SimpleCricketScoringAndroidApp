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
import com.example.cricketscoringapp.Models.Tournament;
import com.example.cricketscoringapp.R;
import com.example.cricketscoringapp.ScorecardActivity;
import com.example.cricketscoringapp.TournamentInfoActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TournamentAdapterForMain extends RecyclerView.Adapter<TournamentAdapterForMain.TournamentViewHolderMain>{

    private List<Tournament> tournaments;
    private SimpleDateFormat dateFormat;
    private Context context;

    public TournamentAdapterForMain(List<Tournament> tournaments, Context context) {
        this.tournaments = tournaments;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @NonNull
    @Override
    public TournamentAdapterForMain.TournamentViewHolderMain onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tournament_item, parent, false);
        return new TournamentAdapterForMain.TournamentViewHolderMain(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TournamentAdapterForMain.TournamentViewHolderMain holder, int position) {
        Tournament tournament = tournaments.get(position);
        holder.tournamentName.setText(tournament.getName());
        holder.location.setText(tournament.getLocation());
        holder.startDate.setText(tournament.getStartDate());
        holder.endDate.setText(tournament.getEndDate());

        // Calculate and set the tournament status
        String status = getTournamentStatus(tournament.getEndDate());
        holder.tournamentStatus.setText(status);
        holder.tournamentStatus.setTextColor(status.equals("Ongoing") ? holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark) : holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisplayPublicMatcheOfTournamentActivity.class);
                intent.putExtra("TOURNAMENT_ID", tournament.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return tournaments.size();
    }

    public class TournamentViewHolderMain extends RecyclerView.ViewHolder {
        TextView tournamentName, location, startDate, endDate, tournamentStatus;
        public TournamentViewHolderMain(@NonNull View itemView) {
            super(itemView);

            tournamentName = itemView.findViewById(R.id.tournamentName);
            location = itemView.findViewById(R.id.location);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            tournamentStatus = itemView.findViewById(R.id.tournamentStatus);
        }

    }
    private String getTournamentStatus(String endDate) {
        try {
            Date end = dateFormat.parse(endDate);
            Date now = new Date();
            if (end != null && end.after(now)) {
                return "Ongoing";
            } else {
                return "Past";
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
