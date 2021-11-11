package com.example.pokemonapp.activities.databases_navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.moves.MovesDatabaseActivity;
import com.example.pokemonapp.activities.databases_navigation.pokemon.PokemonDatabaseActivity;
import com.example.pokemonapp.activities.databases_navigation.types.TypesDatabaseActivity;

public class DatabasesActivity extends AppCompatActivity {

    private CardView pokemonDbButton;
    private CardView movesDbButton;
    private CardView typesDbButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_databases);

        pokemonDbButton = findViewById(R.id.pokemon_db_button);
        movesDbButton = findViewById(R.id.moves_db_button);
        typesDbButton = findViewById(R.id.types_db_button);

        pokemonDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PokemonDatabaseActivity.class));
            }
        });

        movesDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MovesDatabaseActivity.class));
            }
        });

        typesDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TypesDatabaseActivity.class));
            }
        });

    }

}