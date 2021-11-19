package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.enums.GameMode.FAVORITE_TEAM;
import static com.example.pokemonapp.enums.GameMode.RANDOM;
import static com.example.pokemonapp.enums.GameMode.STRATEGY;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.ButtonsActivity;
import com.example.pokemonapp.enums.GameMode;
import com.example.pokemonapp.models.RoundedButton;

public class GameModeSelectionActivity extends ButtonsActivity {

    private GameMode gameMode;

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
//                        gameMode = FAVORITE_TEAM;
//                        nextActivity();
                        Toast.makeText(getApplicationContext(), "Favorite team mode", Toast.LENGTH_SHORT).show();
                    }
                });
        RoundedButton strategyMode = new RoundedButton(getResources().getString(R.string.strategy_mode_button_text),
                getResources().getColor(R.color.moves_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        gameMode = STRATEGY;
//                        nextActivity();
                        Toast.makeText(getApplicationContext(), "Strategy mode", Toast.LENGTH_SHORT).show();
                    }
                });
        RoundedButton randomMode = new RoundedButton(getResources().getString(R.string.random_mode_button_text),
                getResources().getColor(R.color.types_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        gameMode = RANDOM;
//                        nextActivity();
                        Toast.makeText(getApplicationContext(), "Random mode", Toast.LENGTH_SHORT).show();
                    }
                });
        buttons.add(favoriteTeamMode);
        buttons.add(strategyMode);
        buttons.add(randomMode);
    }

    private void nextActivity(){
        Intent intent = new Intent(this,PokemonSelectionActivity.class);
        intent.putExtra("gameMode",gameMode);
        startActivity(intent);
    }
}