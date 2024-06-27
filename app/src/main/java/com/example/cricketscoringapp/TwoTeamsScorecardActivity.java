package com.example.cricketscoringapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.cricketscoringapp.FragmentsPackage.FirstTeamScorecardFragment;
import com.example.cricketscoringapp.FragmentsPackage.SecondTeamScorecardFragment;
import com.google.android.material.tabs.TabLayout;

public class TwoTeamsScorecardActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_teams_scorecard);

        matchId = getIntent().getStringExtra("matchId");

        tabLayout = findViewById(R.id.tabLayoutTowteamsscorecard);
        viewPager = findViewById(R.id.viewPagerTowteamsscorecard);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return FirstTeamScorecardFragment.newInstance(matchId);
                    case 1:
                        return SecondTeamScorecardFragment.newInstance(matchId);
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                switch (position) {
                    case 0:
                        return "First Team";
                    case 1:
                        return "Second Team";
                    default:
                        return null;
                }
            }
        });

        tabLayout.setupWithViewPager(viewPager);
    }
}