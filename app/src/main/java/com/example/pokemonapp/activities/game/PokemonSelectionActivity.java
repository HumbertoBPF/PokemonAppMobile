package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonSelectionActivity extends SelectionActivity {

    private PokemonDAO pokemonDAO;
    private List<Pokemon> pokemonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        titleAppbar = "Pokémon selection";
        nextActivityButtonText = "Go to move selection";
        nextActivity = MovesSelectionActivity.class;

        super.onCreate(savedInstanceState);

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
                configureRecyclerView(playerRecyclerView, getResources().getString(R.string.filename_json_player_team));
                configureRecyclerView(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                configureNextActivityButton();
            }
        }).execute();
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