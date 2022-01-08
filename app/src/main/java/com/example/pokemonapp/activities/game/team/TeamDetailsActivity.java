package com.example.pokemonapp.activities.game.team;

import static com.example.pokemonapp.util.Tools.getInGamePokemonFromJSON;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.models.InGamePokemon;

import java.util.List;

public class TeamDetailsActivity extends AppCompatActivity {

    private RecyclerView teamToLoadRecyclerView;
    private Button loadTeamButton;  // button to confirm that the shown team is indeed the team that the user wants to choose

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_details);

        teamToLoadRecyclerView = findViewById(R.id.team_to_load_recycler_view);
        loadTeamButton = findViewById(R.id.load_team_button);

        Intent intent = getIntent();
        Team team = (Team) intent.getSerializableExtra(getString(R.string.key_extra_db_resource));

        List<InGamePokemon> inGamePokemonList = getInGamePokemonFromJSON(team);

        showSelectedTeamDetails(team, inGamePokemonList);
        configureLoadButton(inGamePokemonList);

        if (intent.getBooleanExtra("hideButton",false)){
            loadTeamButton.setVisibility(View.GONE);
        }
    }

    /**
     * Show the details of the team previously selected. The details consist of info about the
     * pokémon of this team and their moves.
     * @param team concerned Team object.
     * @param inGamePokemonList list of InGamePokemon of the selected team.
     */
    private void showSelectedTeamDetails(Team team, List<InGamePokemon> inGamePokemonList) {
        setTitle(team.getName());
        teamToLoadRecyclerView.setAdapter(new PokemonMovesAdapter(this, inGamePokemonList));
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