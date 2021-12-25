package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.MoveType;
import com.example.pokemonapp.entities.Type;

import java.util.List;

@Dao
public interface MoveTypeDAO extends PokemonAppDAO<MoveType> {

    @Query("SELECT * FROM move_type")
    List<MoveType> getAllMoveTypeRelationshipsFromLocal();

    @Query("SELECT * FROM type INNER JOIN move_type ON type.fId = move_type.typeId WHERE move_type.moveId = :moveId")
    List<Type> getTypesOfMove(long moveId);

    @Query("SELECT COUNT(*) FROM move_type;")
    Long getNbOfElements();

    @Query("SELECT COUNT(*) FROM move_type WHERE move_type.typeId = :typeId")
    Integer getNbMovesWithThisType(long typeId);

}
