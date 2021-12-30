package com.example.pokemonapp.activities.databases_navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.ButtonsActivity;
import com.example.pokemonapp.activities.databases_navigation.moves.MovesDatabaseActivity;
import com.example.pokemonapp.activities.databases_navigation.pokemon.PokemonDatabaseActivity;
import com.example.pokemonapp.activities.databases_navigation.types.TypesDatabaseActivity;
import com.example.pokemonapp.models.RoundedButton;

public class DatabasesActivity extends ButtonsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getString(R.string.databases_button_text));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void declareButtons() {
        RoundedButton pokemonDbButton = new RoundedButton(getString(R.string.title_appbar_pokemon_db),
                getResources().getColor(R.color.pokemon_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), PokemonDatabaseActivity.class));
                    }
                });
        RoundedButton movesDbButton = new RoundedButton(getString(R.string.title_appbar_moves_db),
                getResources().getColor(R.color.moves_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), MovesDatabaseActivity.class));
                    }
                });
        RoundedButton typesDbButton = new RoundedButton(getString(R.string.title_appbar_types_db),
                getResources().getColor(R.color.types_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), TypesDatabaseActivity.class));
                    }
                });
        buttons.add(pokemonDbButton);
        buttons.add(movesDbButton);
        buttons.add(typesDbButton);
    }

}