package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.makeSelector;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.adapters.MovesAdapter;
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
    private List<InGamePokemon> playerTeam;
    private int currentPokemonIndex = 0;    // index of the player's pokémon whose moves are being currenly chosen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonMoveDAO = PokemonAppDatabase.getInstance(this).getPokemonMoveDAO();
        nextActivity = GameActivity.class;

        super.onCreate(savedInstanceState);

        if (gameMode.equals(getResources().getString(R.string.label_random_mode))){
            saveRandomMoves(playerRecyclerView,getString(R.string.filename_json_player_team));
            saveRandomMoves(cpuRecyclerView,getString(R.string.filename_json_cpu_team));
        }else if (gameMode.equals(getResources().getString(R.string.label_favorite_team_mode))){
            playerTeam = loadTeam(this, getResources().getString(R.string.filename_json_player_team));
            playerTeamLabel.setVisibility(View.GONE);
            cpuTeamLabel.setVisibility(View.GONE);

            chooseMovesForCurrentPokemon();
        }
    }

    private void chooseMovesForCurrentPokemon() {
        instructionTextView.setVisibility(View.VISIBLE);
        instructionTextView.setText("Choose the moves of "+playerTeam.get(currentPokemonIndex).getPokemonServer().getFName()+
                " :");

        loadingDialog.show();
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {  // get all the moves of the current pokémon
                return getPokemonMoves(playerTeam.get(currentPokemonIndex));
            }

            @Override
            public void onPostExecute(List<Object> objects) {   // show the moves in a RecyclerView so as the user can choose at most 4
                playerRecyclerView.setAdapter(new MovesAdapter(getApplicationContext(), objects, new MovesAdapter.OnClickListener() {
                    @Override
                    public void onClick(View view, Move move) {
                        if (playerTeam.get(currentPokemonIndex).getMoves().contains(move)){
                            view.setBackground(makeSelector(getResources().getColor(R.color.white),0.8f));
                            playerTeam.get(currentPokemonIndex).removeMove(move);
                        }else{
                            if (playerTeam.get(currentPokemonIndex).getMoves().size() >= 4){
                                Toast.makeText(getApplicationContext(),"You can choose at most 4 moves",Toast.LENGTH_LONG).show();
                            }else{
                                view.setBackground(makeSelector(getResources().getColor(R.color.selection_gray),1.0f));
                                playerTeam.get(currentPokemonIndex).addMove(move);
                            }
                        }
                    }
                }));
                configureConfirmChoiceButton();
                dismissDialogWhenViewIsDrawn(playerRecyclerView, loadingDialog);
            }
        }).execute();
    }

    /**
     * Configuration of the button when the players can use it to confirm their pokémon move choice.
     */
    private void configureConfirmChoiceButton(){
        nextActivityButton.setBackgroundColor(getResources().getColor(R.color.red));
        nextActivityButton.setText("Done");
        nextActivityButton.setVisibility(View.VISIBLE);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootScrollView.smoothScrollTo(0,0);
                if (currentPokemonIndex < playerTeam.size()-1){ // if the player still has pokémon moves to choose,
                                                                // go to the next pokémon
                    currentPokemonIndex++;
                    nextActivityButton.setVisibility(View.GONE);
                    chooseMovesForCurrentPokemon();
                }else{  // if the players has selected the moves for all their pokémon, save it and pick random moves for the CPU
                    playerTeamLabel.setVisibility(View.VISIBLE);
                    cpuTeamLabel.setVisibility(View.VISIBLE);
                    instructionTextView.setVisibility(View.GONE);

                    saveTeam(getApplicationContext(),getResources().getString(R.string.filename_json_player_team),playerTeam);
                    playerRecyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(), playerTeam));

                    if (gameLevel.equals(getString(R.string.easy_level))){
                        saveRandomMoves(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                    }else{
                        saveBestMoves(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                    }
                }
            }
        });
    }

    /**
     * Picks a list of random moves for all the pokémon present in the Shared Preferences for the
     * specified trainer and updates the Shared Preferences file with it.
     * @param recyclerView RecyclerView where the picked moves are going to be presented at the end
     *                     of the selection.
     * @param key key of the Shared Preferences file containing the trainers' info.
     */
    private void saveRandomMoves(RecyclerView recyclerView, String key){
        List<InGamePokemon> inGamePokemonList = loadTeam(this, key);    // get list of pokémon
        loadingDialog.show();
        for (InGamePokemon inGamePokemon : inGamePokemonList){                 // iterates over all the pokémon
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {                          // get list of moves of the current pokémon
                    return getPokemonMoves(inGamePokemon);
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    List<Move> movesInGamePokemon = new ArrayList<>();
                    List<Integer> indexes = new ArrayList<>();
                    if (objects.size()>0){                                      // if the pokémon has any move, we pick at most 4
                                                                                // (if it has less than 4, we get all)
                        indexes = getDistinctRandomIntegers(0,objects.size()-1,
                                Math.min(objects.size(),4));
                    }
                    for (int index : indexes){                                  // add the selected moves to the list of moves
                        movesInGamePokemon.add((Move) objects.get(index));
                    }
                    inGamePokemon.setMoves(movesInGamePokemon);                 // set the list of moves of the current pokémon
                    if (inGamePokemonList.indexOf(inGamePokemon) == inGamePokemonList.size() - 1){
                        saveTeam(getApplicationContext(),key,inGamePokemonList);    // when all the pokémon have been set, save in
                                                                                    // Shared Preferences
                        recyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(),
                                inGamePokemonList));                                // and show the list of moves of each pokémon
                        // the moves of the CPU are chosen lastly, so we are done when the CPU's moves are saved
                        if (key.equals(getResources().getString(R.string.filename_json_cpu_team))){
                            configureNextActivityButton("Start battle");
                            dismissDialogWhenViewIsDrawn(cpuRecyclerView,loadingDialog);
                        }
                    }
                }
            }).execute();
        }
    }

    private void saveBestMoves(RecyclerView recyclerView, String key){
        List<InGamePokemon> inGamePokemonList = loadTeam(this, key);    // get list of pokémon
        loadingDialog.show();
        for (InGamePokemon inGamePokemon : inGamePokemonList){                 // iterates over all the pokémon
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {                          // get list of moves of the current pokémon ordered
                                                                                // by power
                    List<Move> bestMoves = pokemonMoveDAO.getBestMovesOfPokemon(inGamePokemon.getPokemonServer().getFId());
                    List<Object> objects = new ArrayList<>();
                    objects.addAll(bestMoves);
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    List<Move> movesInGamePokemon = new ArrayList<>();
                    for (Object o : objects){                                  // add the best moves to the list of moves
                        movesInGamePokemon.add((Move) o);
                    }
                    inGamePokemon.setMoves(movesInGamePokemon);                 // set the list of moves of the current pokémon
                    if (inGamePokemonList.indexOf(inGamePokemon) == inGamePokemonList.size() - 1){
                        saveTeam(getApplicationContext(),key,inGamePokemonList);    // when all the pokémon have been set, save in
                        // Shared Preferences
                        recyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(),
                                inGamePokemonList));                                // and show the list of moves of each pokémon
                        // the moves of the CPU are chosen lastly, so we are done when the CPU's moves are saved
                        if (key.equals(getResources().getString(R.string.filename_json_cpu_team))){
                            configureNextActivityButton("Start battle");
                            dismissDialogWhenViewIsDrawn(cpuRecyclerView,loadingDialog);
                        }
                    }
                }
            }).execute();
        }
    }

    @NonNull
    private List<Object> getPokemonMoves(InGamePokemon inGamePokemon) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(pokemonMoveDAO.getMovesOfPokemon(inGamePokemon.getPokemonServer().getFId()));
        return objects;
    }

}