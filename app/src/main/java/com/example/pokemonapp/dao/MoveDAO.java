package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Move;

import java.util.List;

@Dao
public interface MoveDAO extends PokemonAppDAO<Move>{

    @Query("SELECT * FROM Move")
    List<Move> getAllMovesFromLocal();

    @Query("SELECT * FROM Move WHERE fName = :name LIMIT 1")
    Move getMoveByName(String name);

    @Query("SELECT COUNT(*) FROM Move;")
    Long getNbOfElements();

}
