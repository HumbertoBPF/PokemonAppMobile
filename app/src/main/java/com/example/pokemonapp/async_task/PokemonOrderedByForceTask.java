package com.example.pokemonapp.async_task;

import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

/**
 * Returns all the pokémon ordered(desc) by Overall Points.
 */
public class PokemonOrderedByForceTask extends DatabaseRecordsTask<Pokemon>{
    /**
     * @param baseDAO                     DAO allowing to communicate with the database containing the entity <b>E</b>.
     * @param onResultListener interface to specify what should be done after fetching.
     */
    public PokemonOrderedByForceTask(PokemonDAO baseDAO, OnResultListener<List<Pokemon>> onResultListener) {
        super(baseDAO, onResultListener);
    }

    @Override
    protected List<Pokemon> doInBackground(Void... voids) {
        return ((PokemonDAO) baseDAO).getPokemonGreatestOverallPoints();
    }
}
