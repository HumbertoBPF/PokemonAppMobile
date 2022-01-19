package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.PokemonType;
import com.example.pokemonapp.entities.Type;

import java.util.List;

@Dao
public abstract class PokemonTypeDAO extends RemoteDAO<PokemonType>{

    public PokemonTypeDAO() {
        super("pokemon_type");
    }

    @Query("SELECT * FROM type INNER JOIN pokemon_type ON type.fId = pokemon_type.typeId WHERE pokemon_type.pokemonId = :pokemonId")
    public abstract List<Type> getTypesOfPokemon(long pokemonId);

    @Query("SELECT type.fId FROM type INNER JOIN pokemon_type ON type.fId = pokemon_type.typeId WHERE pokemon_type.pokemonId = :pokemonId")
    public abstract List<Long> getTypesOfPokemonIds(long pokemonId);

    @Query("SELECT COUNT(*) FROM pokemon_type WHERE pokemon_type.typeId = :typeId")
    public abstract Integer getNbPokemonWithThisType(long typeId);

    public AsyncTask<Void,Void,List<Type>> TypesOfPokemonTask(Pokemon pokemon, OnResultListener<List<Type>> onResultListener){
        return new AsyncTask<Void, Void, List<Type>>() {
            @Override
            protected List<Type> doInBackground(Void... voids) {
                return getTypesOfPokemon(pokemon.getFId());
            }

            @Override
            protected void onPostExecute(List<Type> types) {
                super.onPostExecute(types);
                onResultListener.onResult(types);
            }
        };
    }

    public AsyncTask<Void,Void,Integer> nbPokemonWithThisTypeTask(Type type, OnResultListener<Integer> onResultListener){
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return getNbPokemonWithThisType(type.getFId());
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                onResultListener.onResult(integer);
            }
        };
    }

}
