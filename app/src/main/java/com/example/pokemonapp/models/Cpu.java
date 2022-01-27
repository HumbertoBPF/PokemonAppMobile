package com.example.pokemonapp.models;

import static com.example.pokemonapp.models.Trainer.Position.FRONT;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.CpuMoveSelectionTask;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.async_task.OnTaskListener;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class Cpu extends Trainer{

    private List<Integer> pokemonChosenByCPU = new ArrayList<>();   // indexes of the pokémon to be chosen by CPU everytime it is necessary

    public Cpu() {
        trainerName = "Cpu";
    }

    public List<Integer> getPokemonChosenByCPU() {
        return pokemonChosenByCPU;
    }

    public void setPokemonChosenByCPU(List<Integer> pokemonChosenByCPU) {
        this.pokemonChosenByCPU = pokemonChosenByCPU;
    }

    /**
     * Picks the next available pokémon for the CPU and updates the pokémon data UI.
     */
    public void pickPokemon(Context context, TextView gameDescription){
        if (!pokemonChosenByCPU.isEmpty()){
            // chooses the next pokémon available and removes its index from the list 'pokemonChosenByCPU'
            setCurrentPokemon(getTeam().get(pokemonChosenByCPU.get(0)));
            pokemonChosenByCPU.remove(0);
            gameDescription.setText(context.getString(R.string.cpu_chooses)+getCurrentPokemon().getPokemonServer().getFName());
            // updates pokémon data UI after a while
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPokemonImageResource(context,FRONT);
                    setHpBar(context);
                    updateCurrentPokemonName();
                }
            },3000);
        }else{
            Toast.makeText(context,"The game is finished",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Manage the choice of a move for the CPU. This choice is random when there are remaining moves
     * (i.e. moves whose number of pps is greater than 0). Otherwise, the move 'Struggle' is picked.
     * @param onTaskListener code to be executed after the choice of a move for the CPU.
     */
    public void pickMove(Context context, Player player, String gameLevel, OnTaskListener onTaskListener){
        if (!isLoading()) {
            List<Move> moves = getRemainingMoves(getCurrentPokemon());
            if (moves.isEmpty()){   // if there is no move remaining, use struggle
                PokemonAppDatabase.getInstance(context).getMoveDAO().getStruggleMoveTask(new OnResultListener<Move>() {
                    @Override
                    public void onResult(Move result) {
                        setCurrentMove(result);
                        onTaskListener.onTask();
                    }
                }).execute();
            }else{                  // else, pick a move in the list of available moves
                new CpuMoveSelectionTask(context, moves, player.getCurrentPokemon(), getCurrentPokemon(), gameLevel,
                        new OnResultListener<Move>() {
                            @Override
                            public void onResult(Move result) {
                                setCurrentMove(result);
                                onTaskListener.onTask();
                            }
                        }).execute();
            }
        }else{  // if pokémon is loading an attack or reloading, skips this step
            onTaskListener.onTask();
        }
    }

}
