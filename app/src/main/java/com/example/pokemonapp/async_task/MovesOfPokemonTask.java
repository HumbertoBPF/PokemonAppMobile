package com.example.pokemonapp.async_task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.List;

public class MovesOfPokemonTask extends AsyncTask<Void,Void, List<Move>> {

    private Context context;
    private Pokemon pokemon;
    private OnResultListener<List<Move>> onResultListener;

    public MovesOfPokemonTask(Context context, Pokemon pokemon, OnResultListener<List<Move>> onResultListener) {
        this.context = context;
        this.pokemon = pokemon;
        this.onResultListener = onResultListener;
    }

    @Override
    protected List<Move> doInBackground(Void... voids) {
        return PokemonAppDatabase.getInstance(context).getPokemonMoveDAO().getMovesOfPokemon(pokemon.getFId());
    }

    @Override
    protected void onPostExecute(List<Move> moves) {
        super.onPostExecute(moves);
        onResultListener.onResult(moves);
    }

}
