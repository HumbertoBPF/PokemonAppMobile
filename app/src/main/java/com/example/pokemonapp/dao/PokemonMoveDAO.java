package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.PokemonMove;

import java.util.List;

@Dao
public abstract class PokemonMoveDAO extends RemoteDAO<PokemonMove>{

    public PokemonMoveDAO() {
        super("pokemon_moves");
    }

    @Query("SELECT * FROM move INNER JOIN pokemon_moves ON move.fId = pokemon_moves.moveId" +
            " WHERE pokemon_moves.pokemonId = :pokemonId")
    public abstract List<Move> getMovesOfPokemon(long pokemonId);

    @Query("SELECT * FROM move INNER JOIN pokemon_moves ON move.fId = pokemon_moves.moveId" +
            " WHERE (pokemon_moves.pokemonId = :pokemonId AND move.fUserFaints = 0) ORDER BY move.fPower DESC")
    public abstract List<Move> getStrongestMovesOfPokemon(long pokemonId);

    @Query("SELECT COUNT(*) FROM pokemon_moves;")
    public abstract Long getNbOfElements();

    public AsyncTask<Void,Void,List<Move>> getMovesOfPokemonTask(Pokemon pokemon,
                                                                 OnResultListener<List<Move>> onResultListener){
        return new AsyncTask<Void, Void, List<Move>>() {
            @Override
            protected List<Move> doInBackground(Void... voids) {
                return getMovesOfPokemon(pokemon.getFId());
            }

            @Override
            protected void onPostExecute(List<Move> moves) {
                super.onPostExecute(moves);
                onResultListener.onResult(moves);
            }

        };
    }

}
