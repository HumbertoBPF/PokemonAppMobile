package com.example.pokemonapp.activities.game;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pokemonapp.R;
import com.example.pokemonapp.enums.GameMode;

public class PokemonSelectionActivity extends AppCompatActivity {

    private GameMode gameMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gameMode = (GameMode) getIntent().getSerializableExtra("gameMode");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_selection);

    }

    private void cpuPokemonChoices(){

    }

}