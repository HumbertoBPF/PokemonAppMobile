package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Type;

@Dao
public abstract class TypeDAO extends RemoteDAO<Type>{

    public TypeDAO() {
        super("Type");
    }

    @Query("SELECT COUNT(*) FROM Type;")
    public abstract Long getNbOfElements();

}
