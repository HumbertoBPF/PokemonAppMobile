package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonSelectionActivity extends AppCompatActivity {

    private String gameMode;
    private PokemonDAO pokemonDAO;
    private List<Pokemon> pokemonList;
    private RecyclerView playerRecyclerView;
    private RecyclerView cpuRecyclerView;
    private Button nextActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        setTitle("Pokémon selection");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_selection);

        getLayoutElements();

        SharedPreferences sh = getSharedPreferences(getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        gameMode = sh.getString(getResources().getString(R.string.key_game_mode),null);

        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                pokemonList = pokemonDAO.getPokemonFromLocal();
                return null;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                getRandomPokemon(getResources().getString(R.string.filename_json_player_team));
                getRandomPokemon(getResources().getString(R.string.filename_json_cpu_team));
                configureRecyclerView(playerRecyclerView,
                        getResources().getString(R.string.filename_json_player_team));
                configureRecyclerView(cpuRecyclerView,
                        getResources().getString(R.string.filename_json_cpu_team));
                configureNextActivityButton();
            }
        }).execute();
    }

    private void configureNextActivityButton() {
        nextActivityButton.setVisibility(View.VISIBLE);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MovesSelectionActivity.class));
                finish();
            }
        });
    }

    private void getLayoutElements() {
        nextActivityButton = findViewById(R.id.next_activity_button);
        playerRecyclerView = findViewById(R.id.player_recycler_view);
        cpuRecyclerView = findViewById(R.id.cpu_recycler_view);
    }

    private void configureRecyclerView(RecyclerView recyclerView, String filenameJson) {
        List<Pokemon> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : loadTeam(getApplicationContext(), filenameJson)){
            pokemonList.add(inGamePokemon.getPokemonServer());
        }
        recyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                pokemonList,
                new PokemonAdapter.OnClickListener() {
                    @Override
                    public void onClick(Pokemon pokemon) {

                    }
        }));
    }

    private void getRandomPokemon(String key){
        List<Integer> indexes = getDistinctRandomIntegers(0,pokemonList.size()-1,6);
        List<InGamePokemon> team = new ArrayList<>();
        for (Integer index : indexes){
            team.add(new InGamePokemon((Pokemon) pokemonList.get(index)));
        }
        saveTeam(getApplicationContext(), key, team);
    }

}