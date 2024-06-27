package com.example.cricketscoringapp.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.Models.Player;
import com.example.cricketscoringapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private List<Player> playerList;
    private List<Player> newPlayerList;
    private DatabaseReference databasePlayers;
    private Context context;

    public PlayerAdapter(List<Player> playerList, List<Player> newPlayerList, DatabaseReference databasePlayers, Context context) {
        this.playerList = playerList;
        this.newPlayerList = newPlayerList;
        this.databasePlayers = databasePlayers;
        this.context = context;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_item, parent, false);
        return new PlayerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.playerNameTextView.setText(player.getName());

        holder.deletePlayerButton.setOnClickListener(v -> {
            if (newPlayerList.contains(player)) {
                // Local player deletion
                newPlayerList.remove(player);
                playerList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Player removed from list", Toast.LENGTH_SHORT).show();
            } else {
                // Database player deletion
                databasePlayers.orderByChild("name").equalTo(player.getName()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DataSnapshot snapshot : task.getResult().getChildren()) {
                            snapshot.getRef().removeValue();
                        }
                        playerList.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(context, "Player deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Failed to delete player", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView playerNameTextView;
        ImageButton deletePlayerButton;

        PlayerViewHolder(View itemView) {
            super(itemView);
            playerNameTextView = itemView.findViewById(R.id.playerNameTextView);
            deletePlayerButton = itemView.findViewById(R.id.deletePlayerButton);
        }
    }
}