package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.goToNextActivityWithStringExtra;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
                        goToNextActivityWithStringExtra(GameModeSelectionActivity.this,getString(R.string.key_game_mode),
                                getString(R.string.label_favorite_team_mode), PokemonSelectionActivity.class);
                    }
                });
        RoundedButton strategyMode = new RoundedButton(getResources().getString(R.string.strategy_mode_button_text),
                getResources().getColor(R.color.moves_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        goToNextActivityWithStringExtra(getApplicationContext(),getString(R.string.key_game_mode),
//                                getString(R.string.label_strategy_mode), PokemonSelectionActivity.class);
                        Toast.makeText(getApplicationContext(),"Not available for the moment",Toast.LENGTH_LONG).show();
                    }
                });
        RoundedButton randomMode = new RoundedButton(getResources().getString(R.string.random_mode_button_text),
                getResources().getColor(R.color.types_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNextActivityWithStringExtra(GameModeSelectionActivity.this,getString(R.string.key_game_mode),
                                getString(R.string.label_random_mode), PokemonSelectionActivity.class);
                    }
                });
        buttons.add(favoriteTeamMode);
        buttons.add(strategyMode);
        buttons.add(randomMode);
    }

}