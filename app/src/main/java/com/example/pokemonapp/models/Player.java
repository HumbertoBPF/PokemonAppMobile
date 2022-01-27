package com.example.pokemonapp.models;

import static com.example.pokemonapp.models.Trainer.Position.BACK;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.OnTaskListener;
import com.example.pokemonapp.entities.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class Player extends Trainer{

    /**
     * Asks the player to choose a pokémon by showing and configuring the RecyclerView with the pokémon
     * @param onTaskListener code to be executed after the player's choice.
     */
    public void pickPokemon(Context context, TextView gameDescription, RecyclerView recyclerView,
                                     OnTaskListener onTaskListener){
        // ask the player to choose a pokémon and shows the options
        gameDescription.setText(R.string.choose_pokemon_msg);
        recyclerView.setVisibility(View.VISIBLE);

        recyclerView.setAdapter(new PokemonAdapter(context, getAlivePokemonPlayer(),
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
                                setPokemonImageResource(context,BACK);
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

    /**
     * Returns the list of player's pokémon that are still alive (whose HP is greater than 0).
     */
    @NonNull
    public List<Pokemon> getAlivePokemonPlayer() {
        List<Pokemon> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : getTeam()){
            if (inGamePokemon.getCurrentHp() > 0){
                pokemonList.add(inGamePokemon.getPokemonServer());
            }
        }
        return pokemonList;
    }

}
