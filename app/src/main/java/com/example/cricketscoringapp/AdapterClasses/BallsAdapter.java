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

public class BallsAdapter extends RecyclerView.Adapter<BallsAdapter.ViewHolder> {

    private List<Map<String, Object>> data;

    public BallsAdapter(List<Map<String, Object>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BallsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ballsofover_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BallsAdapter.ViewHolder holder, int position) {
        Map<String, Object> item = data.get(position);

        String wicket = item.get("Wicket") != null ? "W" : "";
        String extraType = item.get("extraType") != null ? String.valueOf(item.get("extraType")) : "";
        String runs = item.get("runs") != null ? String.valueOf(item.get("runs")) : "0";  // Default to "0" if runs is missing

        StringBuilder displayText = new StringBuilder();

        if (!extraType.isEmpty()) {
            displayText.append(extraType);
        }
        if (!wicket.isEmpty()) {
            if (displayText.length() > 0) {
                displayText.append("+");
            }
            displayText.append(wicket);
        }
        if (displayText.length() > 0) {
            displayText.append("+");
        }
        displayText.append(runs);

        holder.specificBallTextview.setText(displayText.toString());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView specificBallTextview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            specificBallTextview = itemView.findViewById(R.id.bowlOfTheOverTextview);

        }
    }
}
