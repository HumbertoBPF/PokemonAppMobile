package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

@Dao
public abstract class PokemonDAO extends RemoteDAO<Pokemon>{

    public PokemonDAO() {
        super("Pokemon");
    }

    @Query("SELECT * FROM Pokemon ORDER BY pokemon.fOverallPts DESC")
    public abstract List<Pokemon> getPokemonGreatestOverallPoints();

    @Query("SELECT COUNT(*) FROM Pokemon;")
    public abstract Long getNbOfElements();

}
