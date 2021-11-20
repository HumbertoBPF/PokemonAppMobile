package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.content.SharedPreferences;
import android.os.Bundle;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_selection);

        playerRecyclerView = findViewById(R.id.player_recycler_view);
        cpuRecyclerView = findViewById(R.id.cpu_recycler_view);

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
                List<Pokemon> playerPokemon = new ArrayList<>();
                List<Pokemon> cpuPokemon = new ArrayList<>();
                for (InGamePokemon inGamePokemon :
                        loadTeam(getApplicationContext(),
                                getResources().getString(R.string.filename_json_player_team))){
                    playerPokemon.add(inGamePokemon.getPokemonServer());
                }
                for (InGamePokemon inGamePokemon :
                        loadTeam(getApplicationContext(),
                                getResources().getString(R.string.filename_json_cpu_team))){
                    cpuPokemon.add(inGamePokemon.getPokemonServer());
                }
                playerRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                        playerPokemon,
                        new PokemonAdapter.OnClickListener() {
                            @Override
                            public void onClick(Pokemon pokemon) {

                            }
                }));
                cpuRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                        cpuPokemon,
                        new PokemonAdapter.OnClickListener() {
                            @Override
                            public void onClick(Pokemon pokemon) {

                            }
                }));
            }
        }).execute();
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