package com.example.cricketscoringapp.AdapterClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.AddTeamPlayersActivity;
import com.example.cricketscoringapp.Models.Team;
import com.example.cricketscoringapp.R;
import com.example.cricketscoringapp.SampleFiles.EditTeamDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

     List<Team> teams;
     Context context;

    public TeamAdapter(Context context, List<Team> teams) {
        this.context = context;
        this.teams = teams;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_item, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teams.get(position);
        holder.teamName.setText(team.getName());

        holder.optionsMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.optionsMenu);
            popupMenu.inflate(R.menu.team_options_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.add_players) {
                    Intent intent = new Intent(context, AddTeamPlayersActivity.class);
                    intent.putExtra("teamId", team.getId());
                    context.startActivity(intent);
                    return true;
                } else if (id == R.id.edit_team_name) {
                    // Open dialog to edit team name
                    editTeamName(team);
                    return true;
                } else if (id == R.id.delete_team) {
                    // Delete team from database
                    deleteTeam(team);
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    private void editTeamName(Team team) {
        // Create and show a dialog to edit team name
        EditTeamDialog editTeamDialog = new EditTeamDialog(context, team);
        editTeamDialog.show();
    }

    private void deleteTeam(Team team) {
        DatabaseReference databaseTeams = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("teams")
                .child(team.getId());
        databaseTeams.removeValue();
    }

    public static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamName;
        ImageView optionsMenu;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.teamName);
            optionsMenu = itemView.findViewById(R.id.teamOptionsMenu);
        }
    }
}