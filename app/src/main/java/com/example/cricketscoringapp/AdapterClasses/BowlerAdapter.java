package com.example.cricketscoringapp.AdapterClasses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cricketscoringapp.R;

import java.util.List;
import java.util.Map;

public class BowlerAdapter extends RecyclerView.Adapter<BowlerAdapter.BowlerViewHolder> {

    private List<Map<String, Object>> bowlerList;

    public BowlerAdapter(List<Map<String, Object>> bowlerList) {
        this.bowlerList = bowlerList;
    }

    @NonNull
    @Override
    public BowlerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bowlers_item, parent, false);
        return new BowlerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BowlerViewHolder holder, int position) {
        Map<String, Object> bowler = bowlerList.get(position);
        holder.nameTextView.setText((String) bowler.get("name"));

        if (bowler.get("wickets") == null){
            holder.wicketsTextView.setText("0");
        } else if (bowler.get("wickets") != null) {
            holder.wicketsTextView.setText(String.valueOf(bowler.get("wickets")));
        }


        holder.runsConcededTextView.setText(String.valueOf(bowler.get("runsConceded")));
        holder.oversTextView.setText(String.valueOf(bowler.get("overs")));
    }

    @Override
    public int getItemCount() {
        return bowlerList.size();
    }

    static class BowlerViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, wicketsTextView, runsConcededTextView, oversTextView;

        public BowlerViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.bowlernameitemtextview);
            wicketsTextView = itemView.findViewById(R.id.wicketstakenbybowleritemtextview);
            runsConcededTextView = itemView.findViewById(R.id.runsconcededbybowleritemtextview);
            oversTextView = itemView.findViewById(R.id.oversofthebowleritemtextview);
        }
    }
}
