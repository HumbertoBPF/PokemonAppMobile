package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.models.InGamePokemon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
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

        Team team = (Team) getIntent().getSerializableExtra("team");

        List<InGamePokemon> inGamePokemonList = getInGamePokemonFromJSON(team);

        showSelectedTeamDetails(team, inGamePokemonList);
        configureLoadButton(inGamePokemonList);

    }

    /**
     * Converts the team attribute of a Team object (it is a JSON String) into a list of pokémon.
     * @param team concerned Team object.
     * @return a list of the pokémon corresponding to team attribute of the specified object.
     */
    @Nullable
    private List<InGamePokemon> getInGamePokemonFromJSON(Team team) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<ArrayList<InGamePokemon>>() {}.getType();
        return gson.fromJson(team.getTeam(), type);
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