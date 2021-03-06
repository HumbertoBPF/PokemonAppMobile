package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.DialogTools.singleButtonDialog;
import static com.example.pokemonapp.util.SharedPreferencesTools.goToNextActivityWithStringExtra;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.ButtonsActivity;
import com.example.pokemonapp.models.RoundedButton;

public class GameModeSelectionActivity extends ButtonsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.choose_mode));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_game_mode_selection, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item_info) {
            Dialog infoDialog = singleButtonDialog(GameModeSelectionActivity.this, getString(R.string.dialog_explain_mode_title),
                    Html.fromHtml(getString(R.string.info_game_mode)), getString(R.string.understood_button_text), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            infoDialog.show();
        }else if (itemId == R.id.item_my_teams){
            startActivity(new Intent(this,LocalDatabasesActivity.class));
        }else if (itemId == R.id.item_config) {
            startActivity(new Intent(this,SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void declareButtons() {
        RoundedButton favoriteTeamMode = new RoundedButton(getString(R.string.favorite_team_mode_button_text),
                getResources().getColor(R.color.pokemon_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNextActivityWithStringExtra(GameModeSelectionActivity.this,getString(R.string.key_game_mode),
                                getString(R.string.label_favorite_team_mode), PokemonSelectionActivity.class);
                    }
                });
        RoundedButton strategyMode = new RoundedButton(getString(R.string.strategy_mode_button_text),
                getResources().getColor(R.color.moves_theme_color),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goToNextActivityWithStringExtra(GameModeSelectionActivity.this, getString(R.string.key_game_mode),
                                getString(R.string.label_strategy_mode), PokemonSelectionActivity.class);
                    }
                });
        RoundedButton randomMode = new RoundedButton(getString(R.string.random_mode_button_text),
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