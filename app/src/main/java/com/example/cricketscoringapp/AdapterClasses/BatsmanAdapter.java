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

public class BatsmanAdapter extends RecyclerView.Adapter<BatsmanAdapter.BatsmanViewHolder>{

    private List<Map<String, Object>> batsmanList;

    public BatsmanAdapter(List<Map<String, Object>> batsmanList) {
        this.batsmanList = batsmanList;
    }

    @NonNull
    @Override
    public BatsmanAdapter.BatsmanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.batsman_item, parent, false);
        return new BatsmanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BatsmanAdapter.BatsmanViewHolder holder, int position) {
        Map<String, Object> batsman = batsmanList.get(position);
        holder.nameTextView.setText((String) batsman.get("name"));
        holder.runsTextView.setText(String.valueOf(batsman.get("runs")));
        holder.ballsTextView.setText(String.valueOf(batsman.get("balls")));
    }

    @Override
    public int getItemCount() {
        return batsmanList.size();
    }

    public class BatsmanViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, runsTextView, ballsTextView;
        public BatsmanViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.batsmanNamefrombatsmansRecviewItem);
            runsTextView = itemView.findViewById(R.id.batsmansRunsFromrecviewItem);
            ballsTextView = itemView.findViewById(R.id.batsmansbowlsfromrecviewitem);

        }
    }
}
