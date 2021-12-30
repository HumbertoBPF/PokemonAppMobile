package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

@Dao
public interface PokemonDAO extends PokemonAppDAO<Pokemon>{

    @Query("SELECT * FROM Pokemon")
    List<Pokemon> getPokemonFromLocal();

    @Query("SELECT * FROM Pokemon ORDER BY pokemon.fOverallPts DESC")
    List<Pokemon> getPokemonGreatestOverallPoints();

    @Query("SELECT COUNT(*) FROM Pokemon;")
    Long getNbOfElements();

}
