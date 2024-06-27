package com.example.cricketscoringapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.AdapterClasses.PlayerAdapter;
import com.example.cricketscoringapp.Models.Player;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddTeamPlayersActivity extends AppCompatActivity {

    private EditText playerNameEditText;
    private Button addPlayerButton, addAllPlayersButton;
    private RecyclerView playersRecyclerView;

    private List<Player> playerList;
    private List<Player> newPlayerList;
    private PlayerAdapter playerAdapter;

    private String teamId;
    private DatabaseReference databasePlayers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team_players);

        playerNameEditText = findViewById(R.id.playerNameEditText);
        addPlayerButton = findViewById(R.id.addPlayerButton);
        addAllPlayersButton = findViewById(R.id.addAllPlayersButton);
        playersRecyclerView = findViewById(R.id.playersRecyclerView);

        playerList = new ArrayList<>();
        newPlayerList = new ArrayList<>();
        teamId = getIntent().getStringExtra("teamId");

        databasePlayers = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("teams")
                .child(teamId)
                .child("players");

        playerAdapter = new PlayerAdapter(playerList, newPlayerList, databasePlayers, this);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playersRecyclerView.setAdapter(playerAdapter);

        // Fetch existing players from the database
        fetchExistingPlayers();

        addPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayer();
            }
        });

        addAllPlayersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAllPlayersToDatabase();
            }
        });
    }

    private void fetchExistingPlayers() {
        databasePlayers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Player player = postSnapshot.getValue(Player.class);
                    playerList.add(player);
                }
                playerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AddTeamPlayersActivity.this, "Failed to load players", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addPlayer() {
        String playerName = playerNameEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(playerName)) {
            Player newPlayer = new Player(playerName);
            playerList.add(newPlayer);
            newPlayerList.add(newPlayer);
            playerAdapter.notifyDataSetChanged();
            playerNameEditText.setText("");
        } else {
            Toast.makeText(this, "Please enter a player name", Toast.LENGTH_SHORT).show();
        }
    }

    private void addAllPlayersToDatabase() {
        for (Player player : newPlayerList) {
            String playerId = databasePlayers.push().getKey();
            if (playerId != null) {
                databasePlayers.child(playerId).setValue(player);
            }
        }
        Toast.makeText(this, "Players added successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}