package com.example.pokemonapp.activities.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.ButtonsActivity;
import com.example.pokemonapp.activities.game.score.ScoreHistoryDatabaseActivity;
import com.example.pokemonapp.activities.game.team.TeamDatabaseActivity;
import com.example.pokemonapp.models.RoundedButton;

/**
 * Activity allowing to select a local entity (i.e. not retrieved from the server) in order to see the
 * list of elements stored on the local the database.
 */
public class LocalDatabasesActivity extends ButtonsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void declareButtons() {
        RoundedButton myTeamsButton = new RoundedButton(getString(R.string.title_appbar_my_teams),
                getResources().getColor(R.color.pokemon_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), TeamDatabaseActivity.class));
                    }
                });
        RoundedButton scoreHistoryButton = new RoundedButton(getString(R.string.title_appbar_scores),
                getResources().getColor(R.color.types_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), ScoreHistoryDatabaseActivity.class));
                    }
                });
        buttons.add(myTeamsButton);
        buttons.add(scoreHistoryButton);
    }
}