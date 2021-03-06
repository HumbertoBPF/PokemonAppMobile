package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.DialogTools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.GeneralTools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.SharedPreferencesTools.loadTeam;
import static com.example.pokemonapp.util.SharedPreferencesTools.saveTeam;
import static com.example.pokemonapp.util.UiTools.makeSelector;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.activities.game.team.SaveTeamActivity;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.PokemonMovesAdapter;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.async_task.SmartMoveSetSelectionTask;
import com.example.pokemonapp.dao.PokemonMoveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MoveSetSelectionActivity extends SelectionActivity {

    private PokemonMoveDAO pokemonMoveDAO;
    private List<InGamePokemon> playerTeam;
    private int currentPokemonIndex = 0;    // index of the player's pokémon whose moves are being currenly chosen
    private boolean canSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDAOs();
        nextActivity = GameActivity.class;

        super.onCreate(savedInstanceState);

        boolean playerTeamReady = getIntent().getBooleanExtra("playerTeamReady",false);

        if (playerTeamReady){
            setTitle(R.string.summary_title_app_bar);

            playerTeam = loadTeam(this, getString(R.string.filename_json_player_team));
            playerRecyclerView.setAdapter(new PokemonMovesAdapter(this, playerTeam));
            if (gameLevel.equals(getString(R.string.easy_level)) || gameMode.equals(getString(R.string.label_random_mode))){
                saveRandomMoves(cpuRecyclerView,getString(R.string.filename_json_cpu_team));
            }else{
                saveBestMoves(cpuRecyclerView,getString(R.string.filename_json_cpu_team));
            }
            return;
        }

        if (gameMode.equals(getString(R.string.label_random_mode))){
            setTitle(R.string.summary_title_app_bar);
            enableSaving(true);     // can save the team for random mode at any moment of this activity

            saveRandomMoves(playerRecyclerView,getString(R.string.filename_json_player_team));
            saveRandomMoves(cpuRecyclerView,getString(R.string.filename_json_cpu_team));
        }else if (gameMode.equals(getString(R.string.label_favorite_team_mode)) ||
            gameMode.equals(getString(R.string.label_strategy_mode))){
            playerTeam = loadTeam(this, getString(R.string.filename_json_player_team));
            playerTeamLabel.setVisibility(View.GONE);
            cpuTeamLabel.setVisibility(View.GONE);
            enableSaving(false);    // cannot save the team before choosing the moves for game modes different from random mode

            chooseMovesForCurrentPokemon();
        }
    }

    /**
     * Shows or hides the saving item on the appbar.
     * @param state whether the item should be shown or hidden.
     */
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_save){
            startActivity(new Intent(this, SaveTeamActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDAOs() {
        pokemonMoveDAO = PokemonAppDatabase.getInstance(this).getPokemonMoveDAO();
    }

    private void chooseMovesForCurrentPokemon() {
        setTitle(getString(R.string.choose_moves_pokemon)+playerTeam.get(currentPokemonIndex).getPokemonServer().getFName());

        loadingDialog.show();
        pokemonMoveDAO.getMovesOfPokemonTask(playerTeam.get(currentPokemonIndex).getPokemonServer(),
                new OnResultListener<List<Move>>() {
                    @Override
                    public void onResult(List<Move> result) {
                        playerRecyclerView.setAdapter(new MovesAdapter(getApplicationContext(), result, new OnItemAdapterClickListener() {
                            @Override
                            public void onClick(View view, Object object) {
                                if (playerTeam.get(currentPokemonIndex).getMoves().contains((Move) object)){
                                    view.setBackground(makeSelector(getResources().getColor(R.color.white),0.8f));
                                    playerTeam.get(currentPokemonIndex).removeMove((Move) object);
                                }else{
                                    if (playerTeam.get(currentPokemonIndex).getMoves().size() >= 4){
                                        Toast.makeText(getApplicationContext(), R.string.max_moves_warning,Toast.LENGTH_LONG).show();
                                    }else{
                                        view.setBackground(makeSelector(getResources().getColor(R.color.selection_gray),1.0f));
                                        playerTeam.get(currentPokemonIndex).addMove((Move) object);
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
            pokemonMoveDAO.getMovesOfPokemonTask(inGamePokemon.getPokemonServer(), new OnResultListener<List<Move>>() {
                @Override
                public void onResult(List<Move> result) {
                    List<Move> movesInGamePokemon = new ArrayList<>();
                    List<Integer> indexes = new ArrayList<>();
                    if (result.size()>0){                                      // if the pokémon has any move, we pick at most 4
                        // (if it has less than 4, we get all)
                        indexes = getDistinctRandomIntegers(0,result.size()-1,
                                Math.min(result.size(),4));
                    }
                    for (int index : indexes){                                  // add the selected moves to the list of moves
                        movesInGamePokemon.add(result.get(index));
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
            new SmartMoveSetSelectionTask(this, inGamePokemon.getPokemonServer(), new OnResultListener<List<Move>>() {
                @Override
                public void onResult(List<Move> result) {
                    inGamePokemon.setMoves(result);                 // set the list of moves of the current pokémon
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
            enableSaving(true);     // after choosing the moves for game modes other than the random one, can save the team
            configureNextActivityButton(getString(R.string.start_battle_button_text));
            dismissDialogWhenViewIsDrawn(cpuRecyclerView, loadingDialog);
        }
    }

}