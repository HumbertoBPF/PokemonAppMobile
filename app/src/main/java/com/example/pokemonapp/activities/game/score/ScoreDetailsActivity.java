package com.example.pokemonapp.activities.game.score;

import static com.example.pokemonapp.util.Tools.getGameLevelStringFromMnemonic;
import static com.example.pokemonapp.util.Tools.getGameModeStringFromMnemonic;
import static com.example.pokemonapp.util.Tools.getInGamePokemonFromJSON;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseDetailsActivity;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.entities.Score;

public class ScoreDetailsActivity extends DatabaseDetailsActivity {

    private Score score;

    private TextView scoreDate;
    private TextView scoreValue;
    private TextView scoreBattleDuration;
    private TextView scoreNbRemainingPokemon;
    private TextView scoreGameInfo;
    private TextView scorePlayerTeamForce;
    private TextView scoreCpuTeamForce;
    private RecyclerView playerTeamRecyclerView;
    private RecyclerView cpuTeamRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        score = (Score) intent.getSerializableExtra(getString(R.string.key_extra_db_resource));
        layout = R.layout.activity_score_details;
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getString(R.string.score_details_appbar_title);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getLayoutElements() {
        scoreDate = findViewById(R.id.score_date);
        scoreValue = findViewById(R.id.score_value);
        scoreBattleDuration = findViewById(R.id.score_battle_duration);
        scoreNbRemainingPokemon = findViewById(R.id.score_nb_remaining_pokemon);
        scoreGameInfo = findViewById(R.id.score_game_info);
        scorePlayerTeamForce = findViewById(R.id.score_player_team_force);
        scoreCpuTeamForce = findViewById(R.id.score_cpu_team_force);
        playerTeamRecyclerView = findViewById(R.id.player_team_recycler_view);
        cpuTeamRecyclerView = findViewById(R.id.cpu_team_recycler_view);
    }

    @Override
    protected void bind() {
        String gameModeString = getGameModeStringFromMnemonic(this,score.getGameMode());
        String gameLevelString = getGameLevelStringFromMnemonic(this,score.getGameLevel());

        scoreDate.setText(score.getDate());
        scoreValue.setText(getString(R.string.score_label)+" : " + score.getScoreValue().toString() + " " + getString(R.string.points));
        scoreBattleDuration.setText(getString(R.string.duration_label)+" : " + score.getBattleDuration().toString() + " " + getString(R.string.seconds));
        scoreNbRemainingPokemon.setText(score.getNbPlayerRemainingPokemon().toString() + " " + getString(R.string.remaining_player_pokemon));
        scoreGameInfo.setText(gameModeString + " - " + gameLevelString);
        scorePlayerTeamForce.setText(getString(R.string.player_team_force_label) + " : " + score.getPlayerTeamOverallPoints() +
                " "+ getString(R.string.overall_points_label));
        scoreCpuTeamForce.setText(getString(R.string.cpu_team_force_label) + " : " + score.getCpuTeamOverallPoints() +
                " "+ getString(R.string.overall_points_label));
        playerTeamRecyclerView.setAdapter(new PokemonMovesAdapter(this,getInGamePokemonFromJSON(score.getPlayerTeam())));
        cpuTeamRecyclerView.setAdapter(new PokemonMovesAdapter(this,getInGamePokemonFromJSON(score.getCpuTeam())));
    }
}