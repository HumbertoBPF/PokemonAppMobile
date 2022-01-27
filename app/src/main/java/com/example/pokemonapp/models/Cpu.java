package com.example.pokemonapp.models;

import static com.example.pokemonapp.models.Trainer.Position.FRONT;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pokemonapp.R;

import java.util.ArrayList;
import java.util.List;

public class Cpu extends Trainer{

    private List<Integer> pokemonChosenByCPU = new ArrayList<>();   // indexes of the pokémon to be chosen by CPU everytime it is necessary

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

}
