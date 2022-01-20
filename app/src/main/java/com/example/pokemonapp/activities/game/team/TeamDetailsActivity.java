package com.example.pokemonapp.activities.game.team;

import static com.example.pokemonapp.util.SharedPreferencesTools.saveTeam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseDetailsActivity;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.models.InGamePokemon;

import java.util.List;

public class TeamDetailsActivity extends DatabaseDetailsActivity {

    private Team team;
    private RecyclerView teamToLoadRecyclerView;
    private Button loadTeamButton;  // button to confirm that the shown team is indeed the team that the user wants to choose
    private boolean hideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        team = (Team) intent.getSerializableExtra(getString(R.string.key_extra_db_resource));
        hideButton = intent.getBooleanExtra("hideButton",false);
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = team.getName();
        layout = R.layout.activity_team_details;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getLayoutElements() {
        teamToLoadRecyclerView = findViewById(R.id.team_to_load_recycler_view);
        loadTeamButton = findViewById(R.id.load_team_button);
    }

    @Override
    protected void bind() {
        List<InGamePokemon> inGamePokemonList = team.getInGamePokemonFromTeam();
        teamToLoadRecyclerView.setAdapter(new PokemonMovesAdapter(this, inGamePokemonList));
        configureLoadButton(inGamePokemonList);

        if (hideButton){
            loadTeamButton.setVisibility(View.GONE);
        }
    }

    /**
     * Configures the button responsible to confirm that the shown team is indeed the team that the
     * user wants to load. When the button is clicked, the team is saved as the player's team in
     * Shared Preferences, the result of this activity is set as RESULT_OK and the current activity
     * is finished.
     * @param inGamePokemonList
     */
    private void configureLoadButton(List<InGamePokemon> inGamePokemonList) {
        loadTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTeam(getApplicationContext(), getString(R.string.filename_json_player_team), inGamePokemonList);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}