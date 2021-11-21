package com.example.pokemonapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;

public abstract class SelectionActivity extends AppCompatActivity {

    protected String gameMode;
    protected RecyclerView playerRecyclerView;
    protected RecyclerView cpuRecyclerView;
    protected Button nextActivityButton;
    protected String titleAppbar;
    protected Class nextActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(titleAppbar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_selection);

        getLayoutElements();

        SharedPreferences sh = getSharedPreferences(getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        gameMode = sh.getString(getResources().getString(R.string.key_game_mode),null);
    }

    protected void getLayoutElements() {
        nextActivityButton = findViewById(R.id.next_activity_button);
        playerRecyclerView = findViewById(R.id.player_recycler_view);
        cpuRecyclerView = findViewById(R.id.cpu_recycler_view);
    }

    protected void configureNextActivityButton() {
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