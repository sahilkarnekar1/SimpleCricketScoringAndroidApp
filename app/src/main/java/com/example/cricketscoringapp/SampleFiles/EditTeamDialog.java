package com.example.cricketscoringapp.SampleFiles;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cricketscoringapp.Models.Team;
import com.example.cricketscoringapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditTeamDialog extends Dialog {

    private Team team;
    private EditText teamNameEditText;
    private Button editButton;

    public EditTeamDialog(@NonNull Context context, Team team) {
        super(context);
        this.team = team;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_team);

        teamNameEditText = findViewById(R.id.teamNameEditTextDialog);
        editButton = findViewById(R.id.editTeamButtonDialog);

        teamNameEditText.setText(team.getName());

        editButton.setOnClickListener(v -> {
            String newName = teamNameEditText.getText().toString().trim();
            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(getContext(), "Please enter a team name", Toast.LENGTH_SHORT).show();
            } else {
                updateTeamName(newName);
            }
        });
    }

    private void updateTeamName(String newName) {
        DatabaseReference databaseTeams = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("teams")
                .child(team.getId());
        databaseTeams.child("name").setValue(newName);
        Toast.makeText(getContext(), "Team name updated", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
