package com.example.cricketscoringapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.example.cricketscoringapp.AdapterClasses.TournamentInfoPagerAdapter;
import com.example.cricketscoringapp.TournamentFragments.AboutFragment;
import com.example.cricketscoringapp.TournamentFragments.MatchesFragment;
import com.example.cricketscoringapp.TournamentFragments.TeamsFragment;
import com.google.android.material.tabs.TabLayout;

public class TournamentInfoActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tournament_info);
        String tournamentId = getIntent().getStringExtra("TOURNAMENT_ID");

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        setupViewPager(viewPager, tournamentId);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager, String tournamentId) {
        TournamentInfoPagerAdapter adapter = new TournamentInfoPagerAdapter(getSupportFragmentManager());

        // Pass the tournament ID to each fragment
        MatchesFragment matchesFragment = new MatchesFragment();
        TeamsFragment teamsFragment = new TeamsFragment();
        AboutFragment aboutFragment = new AboutFragment();

        Bundle bundle = new Bundle();
        bundle.putString("TOURNAMENT_ID", tournamentId);

        matchesFragment.setArguments(bundle);
        teamsFragment.setArguments(bundle);
        aboutFragment.setArguments(bundle);

        adapter.addFragment(matchesFragment, "Matches");
        adapter.addFragment(teamsFragment, "Teams");
        adapter.addFragment(aboutFragment, "About");

        viewPager.setAdapter(adapter);
    }
}