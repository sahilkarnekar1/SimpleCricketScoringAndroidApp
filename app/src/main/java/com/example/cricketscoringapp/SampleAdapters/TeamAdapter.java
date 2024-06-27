package com.example.cricketscoringapp.SampleAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.cricketscoringapp.Models.Team;
import com.example.cricketscoringapp.R;

import java.util.List;

public class TeamAdapter extends ArrayAdapter<Team> {

    public TeamAdapter(Context context, List<Team> teams) {
        super(context, 0, teams);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Team team = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_team2, parent, false);
        }

        TextView teamNameTextView = convertView.findViewById(R.id.teamNameTextView);
        teamNameTextView.setText(team.getName());

        return convertView;
    }
}
