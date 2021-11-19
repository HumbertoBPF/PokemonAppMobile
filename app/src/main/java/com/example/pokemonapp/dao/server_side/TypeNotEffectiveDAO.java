package com.example.pokemonapp.dao.server_side;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.server_side.Type;
import com.example.pokemonapp.entities.server_side.TypeNotEffective;

import java.util.List;

@Dao
public interface TypeNotEffectiveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<TypeNotEffective> typeNotEffectives);

    @Query("SELECT * FROM type INNER JOIN type_not_effective ON type.fId = type_not_effective.notEffectiveId " +
            "WHERE type_not_effective.typeId = :typeId")
    List<Type> getNotEffectiveTypes(long typeId);

}
