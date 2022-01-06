package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.makeSelector;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonMoveDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesSelectionActivity extends SelectionActivity {

    private PokemonMoveDAO pokemonMoveDAO;
    private PokemonTypeDAO pokemonTypeDAO;
    private MoveTypeDAO moveTypeDAO;
    private List<InGamePokemon> playerTeam;
    private int currentPokemonIndex = 0;    // index of the player's pokémon whose moves are being currenly chosen
    private boolean canSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDAOs();
        nextActivity = GameActivity.class;

        super.onCreate(savedInstanceState);

        if (gameMode.equals(getString(R.string.label_random_mode))){
            setTitle(R.string.summary_title_app_bar);
            enableSaving(true);

            saveRandomMoves(playerRecyclerView,getString(R.string.filename_json_player_team));
            saveRandomMoves(cpuRecyclerView,getString(R.string.filename_json_cpu_team));
        }else if (gameMode.equals(getString(R.string.label_favorite_team_mode)) ||
            gameMode.equals(getString(R.string.label_strategy_mode))){
            playerTeam = loadTeam(this, getString(R.string.filename_json_player_team));
            playerTeamLabel.setVisibility(View.GONE);
            cpuTeamLabel.setVisibility(View.GONE);
            enableSaving(false);

            chooseMovesForCurrentPokemon();
        }
    }

    private void enableSaving(boolean state) {
        canSave = state;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemSave = menu.findItem(R.id.item_save);
        itemSave.setVisible(canSave);
        return super.onPrepareOptionsMenu(menu);
    }

    private void getDAOs() {
        pokemonMoveDAO = PokemonAppDatabase.getInstance(this).getPokemonMoveDAO();
        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();
        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();
    }

    private void chooseMovesForCurrentPokemon() {
        setTitle(getString(R.string.choose_moves_pokemon)+playerTeam.get(currentPokemonIndex).getPokemonServer().getFName());

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
                                Toast.makeText(getApplicationContext(), R.string.max_moves_warning,Toast.LENGTH_LONG).show();
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
        nextActivityButton.setText(R.string.done_button_text);
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
                    setTitle(R.string.summary_title_app_bar);

                    saveTeam(getApplicationContext(),getString(R.string.filename_json_player_team),playerTeam);
                    playerRecyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(), playerTeam));

                    if (gameLevel.equals(getString(R.string.easy_level))){
                        saveRandomMoves(cpuRecyclerView,getString(R.string.filename_json_cpu_team));
                    }else{
                        saveBestMoves(cpuRecyclerView,getString(R.string.filename_json_cpu_team));
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
                        showSummary(recyclerView, key, inGamePokemonList);
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
                    List<Move> bestMoves = pokemonMoveDAO.getStrongestMovesOfPokemon(inGamePokemon.getPokemonServer().getFId());

                    List<Type> typesPokemon = pokemonTypeDAO.getTypesOfPokemon(inGamePokemon.getPokemonServer().getFId());

                    Type type1;
                    Type type2 = null;

                    type1 = typesPokemon.get(0);
                    if (typesPokemon.size() > 1){
                        type1 = typesPokemon.get(1);
                        type2 = typesPokemon.get(0);
                    }

                    List<Move> movesStabType1 = new ArrayList<>();  // list for moves whose type is the primary type of the pokémon
                    List<Move> movesStabType2 = new ArrayList<>();  // list for moves whose type is the secondary type of the pokémon
                    List<Move> movesNoStab = new ArrayList<>();     // list for all the other moves

                    for (Move move : bestMoves){    // groups moves according to their type and the type(s) of the pokémon
                        Type typeMove = moveTypeDAO.getTypesOfMove(move.getFId()).get(0);
                        if (typeMove.getFId().equals(type1.getFId())){
                            movesStabType1.add(move);
                        }else if (type2 != null){
                            if (typeMove.getFId().equals(type2.getFId())){
                                movesStabType2.add(move);
                            }else{
                                movesNoStab.add(move);
                            }
                        }else{
                            movesNoStab.add(move);
                        }
                    }

                    List<Object> objects = new ArrayList<>();   // final list of moves
                    List<Integer> indexes;                      // array to store random indexes that are going to be picked
                    int index;                                  // int variable to manipulate the elements of the array above

                    // if there is any move with the secondary type, pick one randomly
                    if (!movesStabType2.isEmpty()){
                        indexes = getDistinctRandomIntegers(0,movesStabType2.size()-1,1);
                        index = indexes.get(0);
                        objects.add(movesStabType2.remove(index));     // the size should be 1 here in the best case
                    }

                    // if there is any move with the primary type, pick one randomly
                    if (!movesStabType1.isEmpty()){
                        indexes = getDistinctRandomIntegers(0,movesStabType1.size()-1,1);
                        index = indexes.get(0);
                        objects.add(movesStabType1.remove(index));     // the size should be 2 here in the best case

                        // if there is no move with the secondary type, the size of the final list of moves is 1 so far. For that case,
                        // if there are any remaining move of the primary type, add the strongest one to the final list
                        if (objects.size() < 2){
                            if (!movesStabType1.isEmpty()){
                                objects.add(movesStabType1.get(0));
                            }
                        }

                    }

                    // if only one move was added so far, it is possible that we have only
                    // one move of the type2 and no move at all of the type1. For such case,
                    // we add a second stab move of the type 2(hence, we will have both stab moves of type2)
                    if (objects.size() < 2){
                        if (!movesStabType2.isEmpty()){
                            objects.add(movesStabType2.get(0));
                        }
                    }


                    // at this point, we complete the list of moves with the no stab moves available
                    if (!movesNoStab.isEmpty()){
                        // we try to complete the list of final moves, but we have to check if we have enough moves to do so
                        // this is the function of the Math.min
                        indexes = getDistinctRandomIntegers(0,movesNoStab.size()-1,
                                Math.min(4-objects.size(),movesNoStab.size()));
                        for (Integer i : indexes){
                            objects.add(movesNoStab.get(i));
                        }
                    }

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
                        showSummary(recyclerView, key, inGamePokemonList);
                    }
                }
            }).execute();
        }
    }

    private void showSummary(RecyclerView recyclerView, String key, List<InGamePokemon> inGamePokemonList) {
        recyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(),
                inGamePokemonList));                                // and show the list of moves of each pokémon
        // the moves of the CPU are chosen lastly, so we are done when the CPU's moves are saved
        if (key.equals(getString(R.string.filename_json_cpu_team))) {
            enableSaving(true);
            configureNextActivityButton(getString(R.string.start_battle_button_text));
            dismissDialogWhenViewIsDrawn(cpuRecyclerView, loadingDialog);
        }
    }

    @NonNull
    private List<Object> getPokemonMoves(InGamePokemon inGamePokemon) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(pokemonMoveDAO.getMovesOfPokemon(inGamePokemon.getPokemonServer().getFId()));
        return objects;
    }

}