package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

@Dao
public abstract class PokemonDAO extends RemoteDAO<Pokemon>{

    public PokemonDAO() {
        super("Pokemon");
    }

    @Query("SELECT * FROM Pokemon ORDER BY pokemon.fOverallPts DESC")
    public abstract List<Pokemon> getPokemonOrderedByForce();

    @Query("SELECT COUNT(*) FROM Pokemon;")
    public abstract Long getNbOfElements();

    public AsyncTask<Void,Void,List<Pokemon>> getPokemonOrderedByForceTask(OnResultListener<List<Pokemon>> onResultListener){
        return new AsyncTask<Void, Void, List<Pokemon>>() {
            @Override
            protected List<Pokemon> doInBackground(Void... voids) {
                return getPokemonOrderedByForce();
            }

            @Override
            protected void onPostExecute(List<Pokemon> records) {
                super.onPostExecute(records);
                onResultListener.onResult(records);
            }
        };
    }

}
