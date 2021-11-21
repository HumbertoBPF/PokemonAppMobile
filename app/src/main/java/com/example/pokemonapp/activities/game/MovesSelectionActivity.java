package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonMoveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesSelectionActivity extends SelectionActivity {

    private PokemonMoveDAO pokemonMoveDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonMoveDAO = PokemonAppDatabase.getInstance(this).getPokemonMoveDAO();
        titleAppbar = "Move selection";
        nextActivity = GameActivity.class;

        super.onCreate(savedInstanceState);

        getRandomMoves(playerRecyclerView,getResources().getString(R.string.filename_json_player_team));
        getRandomMoves(cpuRecyclerView,getResources().getString(R.string.filename_json_cpu_team));
    }

    private void getRandomMoves(RecyclerView recyclerView, String key){
        List<InGamePokemon> inGamePokemonList = loadTeam(this, key);
        for (InGamePokemon inGamePokemon : inGamePokemonList){
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    List<Move> moves = pokemonMoveDAO.
                            getMovesOfPokemon(inGamePokemon.getPokemonServer().getFId());
                    List<Object> objects = new ArrayList<>();
                    objects.addAll(moves);
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    List<Move> movesInGamePokemon = new ArrayList<>();
                    List<Integer> indexes = new ArrayList<>();
                    if (objects.size()>0){
                        indexes = getDistinctRandomIntegers(0,objects.size()-1,
                                Math.min(objects.size(),4));
                    }
                    for (int index : indexes){
                        movesInGamePokemon.add((Move) objects.get(index));
                    }
                    inGamePokemon.setMoves(movesInGamePokemon);
                    if (inGamePokemonList.indexOf(inGamePokemon) == inGamePokemonList.size() - 1){
                        saveTeam(getApplicationContext(),key,inGamePokemonList);
                        recyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(),
                                inGamePokemonList));
                        if (key.equals(getResources().getString(R.string.filename_json_cpu_team))){
                            configureNextActivityButton();
                        }
                    }
                }
            }).execute();
        }
    }

}