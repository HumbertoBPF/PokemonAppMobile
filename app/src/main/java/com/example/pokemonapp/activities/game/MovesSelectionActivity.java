package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonMoveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesSelectionActivity extends AppCompatActivity {

    private String gameMode;
    private PokemonMoveDAO pokemonMoveDAO;
    private RecyclerView playerRecyclerView;
    private RecyclerView cpuRecyclerView;
    private Button nextActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonMoveDAO = PokemonAppDatabase.getInstance(this).getPokemonMoveDAO();
        setTitle("Move selection");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokemon_selection);

        getLayoutElements();

        SharedPreferences sh = getSharedPreferences(getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        gameMode = sh.getString(getResources().getString(R.string.key_game_mode),null);

        getRandomMoves(playerRecyclerView,getResources().getString(R.string.filename_json_player_team));
        getRandomMoves(cpuRecyclerView,getResources().getString(R.string.filename_json_cpu_team));
    }

    private void getLayoutElements() {
        nextActivityButton = findViewById(R.id.next_activity_button);
        playerRecyclerView = findViewById(R.id.player_recycler_view);
        cpuRecyclerView = findViewById(R.id.cpu_recycler_view);
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
                        Log.i("listIndexes",indexes.toString());
                    }
                    for (int index : indexes){
                        movesInGamePokemon.add((Move) objects.get(index));
                    }
                    inGamePokemon.setMoves(movesInGamePokemon);
                    if (inGamePokemonList.indexOf(inGamePokemon) == inGamePokemonList.size() - 1){
                        saveTeam(getApplicationContext(),key,inGamePokemonList);
                        recyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(),
                                inGamePokemonList));
                    }
                }
            }).execute();
        }
    }

}