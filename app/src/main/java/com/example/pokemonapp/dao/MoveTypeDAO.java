package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.MoveType;
import com.example.pokemonapp.entities.Type;

import java.util.List;

@Dao
public abstract class MoveTypeDAO extends RemoteDAO<MoveType> {

    public MoveTypeDAO() {
        super("move_type");
    }

    @Query("SELECT * FROM type INNER JOIN move_type ON type.fId = move_type.typeId WHERE move_type.moveId = :moveId")
    public abstract List<Type> getTypesOfMove(long moveId);

    @Query("SELECT COUNT(*) FROM move_type;")
    public abstract Long getNbOfElements();

    @Query("SELECT COUNT(*) FROM move_type WHERE move_type.typeId = :typeId")
    public abstract Integer getNbMovesWithThisType(long typeId);

}
