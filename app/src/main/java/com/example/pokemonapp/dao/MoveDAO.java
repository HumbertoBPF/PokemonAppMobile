package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Move;

@Dao
public abstract class MoveDAO extends RemoteDAO<Move>{

    public MoveDAO() {
        super("Move");
    }

    @Query("SELECT * FROM Move WHERE fName = :name LIMIT 1")
    public abstract Move getMoveByName(String name);

    @Query("SELECT COUNT(*) FROM Move;")
    public abstract Long getNbOfElements();

}
