package com.example.pokemonapp.activities.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.ButtonsActivity;
import com.example.pokemonapp.models.RoundedButton;

public class GameModeSelectionActivity extends ButtonsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void declareButtons() {
        RoundedButton favoriteTeamMode = new RoundedButton(getResources().getString(R.string.favorite_team_mode_button_text),
                getResources().getColor(R.color.pokemon_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextActivity(getResources().getString(R.string.label_favorite_team_mode));
                    }
                });
        RoundedButton strategyMode = new RoundedButton(getResources().getString(R.string.strategy_mode_button_text),
                getResources().getColor(R.color.moves_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextActivity(getResources().getString(R.string.label_strategy_mode));
                    }
                });
        RoundedButton randomMode = new RoundedButton(getResources().getString(R.string.random_mode_button_text),
                getResources().getColor(R.color.types_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextActivity(getResources().getString(R.string.label_random_mode));
                    }
                });
        buttons.add(favoriteTeamMode);
        buttons.add(strategyMode);
        buttons.add(randomMode);
    }

    private void nextActivity(String gameMode){
        SharedPreferences sharedPreferences = getSharedPreferences(
                getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(getResources().getString(R.string.key_game_mode), gameMode);
        myEdit.apply();

        startActivity(new Intent(this,PokemonSelectionActivity.class));
    }
}