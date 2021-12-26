package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
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
    private int currentPokemonIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonMoveDAO = PokemonAppDatabase.getInstance(this).getPokemonMoveDAO();
        nextActivityButtonText = "Start battle";
        nextActivity = GameActivity.class;

        super.onCreate(savedInstanceState);

        if (gameMode.equals(getResources().getString(R.string.label_random_mode))){
            saveRandomMoves(playerRecyclerView,getResources().getString(R.string.filename_json_player_team));
            saveRandomMoves(cpuRecyclerView,getResources().getString(R.string.filename_json_cpu_team));
        }else if (gameMode.equals(getResources().getString(R.string.label_favorite_team_mode))){
            playerTeam = loadTeam(this, getResources().getString(R.string.filename_json_player_team));
            cpuTeamLabel.setVisibility(View.GONE);

            chooseMovesForCurrentPokemon();
        }
    }

    private void chooseMovesForCurrentPokemon() {
        playerTeamLabel.setText("Choose the moves of "+playerTeam.get(currentPokemonIndex).getPokemonServer().getFName()+
                " :");
        loadingDialog.show();
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Move> moves = pokemonMoveDAO.
                        getMovesOfPokemon(playerTeam.get(currentPokemonIndex).getPokemonServer().getFId());
                List<Object> objects = new ArrayList<>();
                objects.addAll(moves);
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                playerRecyclerView.setAdapter(new MovesAdapter(getApplicationContext(), objects, new MovesAdapter.OnClickListener() {
                    @Override
                    public void onClick(View view, Move move) {
                        if (playerTeam.get(currentPokemonIndex).getMoves().contains(move)){
                            ((CardView) view).setCardBackgroundColor(getResources().getColor(R.color.white));
                            playerTeam.get(currentPokemonIndex).removeMove(move);
                        }else{
                            if (playerTeam.get(currentPokemonIndex).getMoves().size() >= 4){
                                Toast.makeText(getApplicationContext(),"You can choose at most 4 moves",Toast.LENGTH_LONG).show();
                            }else{
                                ((CardView) view).setCardBackgroundColor(getResources().getColor(R.color.selection_gray));
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

    private void configureConfirmChoiceButton(){
        nextActivityButton.setBackgroundColor(getResources().getColor(R.color.red));
        nextActivityButton.setText("Done");
        nextActivityButton.setVisibility(View.VISIBLE);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootScrollView.smoothScrollTo(0,0);
                if (currentPokemonIndex < playerTeam.size()-1){
                    currentPokemonIndex++;
                    nextActivityButton.setVisibility(View.GONE);
                    chooseMovesForCurrentPokemon();
                }else{
                    playerTeamLabel.setText(getResources().getString(R.string.player_team_label));
                    cpuTeamLabel.setVisibility(View.VISIBLE);
                    saveTeam(getApplicationContext(),getResources().getString(R.string.filename_json_player_team),playerTeam);

                    playerRecyclerView.setAdapter(new PokemonMovesAdapter(getApplicationContext(), playerTeam));
                    saveRandomMoves(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                }
            }
        });
    }

    private void saveRandomMoves(RecyclerView recyclerView, String key){
        List<InGamePokemon> inGamePokemonList = loadTeam(this, key);
        loadingDialog.show();
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
                            configureNextActivityButton(true);
                            dismissDialogWhenViewIsDrawn(cpuRecyclerView,loadingDialog);
                        }
                    }
                }
            }).execute();
        }
    }

}