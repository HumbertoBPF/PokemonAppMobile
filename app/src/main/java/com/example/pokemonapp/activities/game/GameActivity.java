package com.example.pokemonapp.activities.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pokemonapp.R;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Game activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}