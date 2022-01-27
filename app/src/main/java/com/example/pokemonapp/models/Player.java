package com.example.pokemonapp.models;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.async_task.OnTaskListener;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Player extends Trainer{

    public Player(List<InGamePokemon> team) {
        super(team);
        this.trainerName = "Player";
    }

    /**
     * Asks the player to choose a pokémon by showing and configuring the RecyclerView with the pokémon
     * @param onTaskListener code to be executed after the player's choice.
     */
    public void pickPokemon(Context context, TextView gameDescription, RecyclerView recyclerView,
                                     OnTaskListener onTaskListener){
        // ask the player to choose a pokémon and shows the options
        gameDescription.setText(R.string.choose_pokemon_msg);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new PokemonAdapter(context, getAlivePokemon(),
                new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        // selects the InGamePokémon corresponding to the selected pokémon (same id)
                        for (InGamePokemon inGamePokemon : getTeam()){
                            if (inGamePokemon.getPokemonServer().getFId().equals(((Pokemon) object).getFId())){
                                setCurrentPokemon(inGamePokemon);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                        // announces player's choice
                        Pokemon pokemonServerPlayer = getCurrentPokemon().getPokemonServer();
                        gameDescription.setText(context.getString(R.string.player_chooses)+pokemonServerPlayer.getFName());
                        // update pokémon data UI after a while
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                setCurrentPokemonImageResource(context);
                                setHpBar(context);
                                updateCurrentPokemonName();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        onTaskListener.onTask();
                                    }
                                },3000);
                            }
                        },3000);
                    }
                })
        );
    }

    public void setCurrentPokemonImageResource(Context context) {
        String pokemonImageName = "pokemon_"+
                getCurrentPokemon().getPokemonServer().getFName().toLowerCase(Locale.ROOT)
                        .replace("'","")
                        .replace(" ","_")
                        .replace(".","") + "_back";
        int imageId = context.getResources().getIdentifier(pokemonImageName,"drawable",context.getPackageName());
        this.currentPokemonImageView.setImageResource(imageId);
    }

    /**
     * Manage the selection of a move for the player's pokémon by either asking the player to select a move
     * when there are remaining moves (i.e. moves whose number of pp is greater than 0) to be selected
     * or selecting 'Struggle' when there is no remaining move.
     * @param onTaskListener code to be executed after the player's choice
     */
    public void pickMove(Context context, TextView gameDescription, RecyclerView playerChoicesRecyclerView,
                         OnTaskListener onTaskListener){
        if (!isLoading()){
            List<Move> moves = getRemainingMoves(getCurrentPokemon());
            if (moves.isEmpty()){   // if there is no move remaining, use struggle
                PokemonAppDatabase.getInstance(context).getMoveDAO().getStruggleMoveTask(new OnResultListener<Move>() {
                    @Override
                    public void onResult(Move result) {
                        setCurrentMove(result);
                        onTaskListener.onTask();
                    }
                }).execute();
            }else{  // else, ask the player to choose a mode by presenting the moves in a RecyclerView
                gameDescription.setText(R.string.choose_move_msg);
                playerChoicesRecyclerView.setVisibility(View.VISIBLE);
                playerChoicesRecyclerView.setAdapter(new MovesAdapter(context, moves, new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        setCurrentMove((Move) object);
                        playerChoicesRecyclerView.setVisibility(View.GONE);
                        onTaskListener.onTask();
                    }
                }));
            }
        }else{  // if pokémon is loading an attack or reloading, skips this step
            onTaskListener.onTask();
        }
    }

    /**
     * Returns the list of player's pokémon that are still alive (whose HP is greater than 0).
     */
    @NonNull
    public List<Pokemon> getAlivePokemon() {
        List<Pokemon> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : getTeam()){
            if (inGamePokemon.getCurrentHp() > 0){
                pokemonList.add(inGamePokemon.getPokemonServer());
            }
        }
        return pokemonList;
    }

}
