package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Type;

import java.util.List;

@Dao
public interface TypeDAO extends PokemonAppDAO<Type>{

    @Query("SELECT * FROM Type")
    List<Type> getAllTypesFromLocal();

    @Query("SELECT COUNT(*) FROM Type;")
    Long getNbOfElements();

}
