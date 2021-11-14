package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.models.MoveType;
import com.example.pokemonapp.models.Type;

import java.util.List;

@Dao
public interface MoveTypeDAO {

    @Insert(onConflict = REPLACE)
    void save(List<MoveType> moveTypes);

    @Query("SELECT * FROM move_type")
    List<MoveType> getAllMoveTypeRelationshipsFromLocal();

    @Query("SELECT * FROM type INNER JOIN move_type ON type.fId = move_type.typeId WHERE move_type.moveId = :moveId")
    List<Type> getTypesOfMove(long moveId);

}
