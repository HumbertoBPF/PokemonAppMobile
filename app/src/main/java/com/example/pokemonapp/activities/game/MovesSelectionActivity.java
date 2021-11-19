package com.example.pokemonapp.activities.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.pokemonapp.R;

public class MovesSelectionActivity extends AppCompatActivity {

    private int indexPokemon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Select the moves of your pokémon");
        indexPokemon = getIntent().getIntExtra("indexPokemon",6);
        if (indexPokemon > 5){
            startActivity(new Intent(this,GameActivity.class));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moves_selection);
    }

}