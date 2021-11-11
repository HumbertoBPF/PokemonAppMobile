package com.example.pokemonapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pokemonapp.R;

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
                startActivity(new Intent(getApplicationContext(),PokemonDatabaseActivity.class));
            }
        });

        movesDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MovesDatabaseActivity.class));
            }
        });

        typesDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),TypesDatabaseActivity.class));
            }
        });

    }

}