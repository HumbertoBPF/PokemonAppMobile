package com.example.pokemonapp.activities;

import static com.example.pokemonapp.util.Tools.loadingDialog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;

public abstract class SelectionActivity extends AppCompatActivity {

    protected String gameMode;
    protected RecyclerView playerRecyclerView;
    protected RecyclerView cpuRecyclerView;
    protected NestedScrollView rootScrollView;
    protected TextView playerTeamLabel;
    protected TextView cpuTeamLabel;
    protected Button nextActivityButton;
    protected String nextActivityButtonText;
    protected String titleAppbar;
    protected Class nextActivity;
    protected ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(titleAppbar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        loadingDialog = loadingDialog(this);

        getLayoutElements();

        SharedPreferences sh = getSharedPreferences(getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        gameMode = sh.getString(getResources().getString(R.string.key_game_mode),null);
    }

    protected void getLayoutElements() {
        rootScrollView = findViewById(R.id.root_scroll_view);
        nextActivityButton = findViewById(R.id.next_activity_button);
        playerRecyclerView = findViewById(R.id.player_recycler_view);
        cpuRecyclerView = findViewById(R.id.cpu_recycler_view);
        playerTeamLabel = findViewById(R.id.player_team_label);
        cpuTeamLabel = findViewById(R.id.cpu_team_label);
    }

    protected void configureNextActivityButton() {
        nextActivityButton.setText(nextActivityButtonText);
        nextActivityButton.setVisibility(View.VISIBLE);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), nextActivity));
                finish();
            }
        });
    }

}