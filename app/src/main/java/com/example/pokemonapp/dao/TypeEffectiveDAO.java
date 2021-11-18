package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.models.Type;
import com.example.pokemonapp.models.TypeEffective;

import java.util.List;

@Dao
public interface TypeEffectiveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<TypeEffective> typeEffectives);

    @Query("SELECT * FROM type INNER JOIN type_effective ON type.fId = type_effective.effectiveId WHERE type_effective.typeId = :typeId")
    List<Type> getEffectiveTypes(long typeId);

}
