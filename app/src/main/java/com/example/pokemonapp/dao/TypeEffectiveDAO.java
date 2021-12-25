package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeEffective;

import java.util.List;

@Dao
public interface TypeEffectiveDAO extends PokemonAppDAO<TypeEffective>{

    @Query("SELECT * FROM type INNER JOIN type_effective ON type.fId = type_effective.effectiveId WHERE type_effective.typeId = :typeId")
    List<Type> getEffectiveTypes(long typeId);

    @Query("SELECT type.fId FROM type INNER JOIN type_effective ON type.fId = type_effective.effectiveId WHERE type_effective.typeId = :typeId")
    List<Long> getEffectiveTypesIds(long typeId);

    @Query("SELECT COUNT(*) FROM type_effective;")
    Long getNbOfElements();

}
