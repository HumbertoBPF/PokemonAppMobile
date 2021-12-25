package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeNotEffective;

import java.util.List;

@Dao
public interface TypeNotEffectiveDAO extends PokemonAppDAO<TypeNotEffective>{

    @Query("SELECT * FROM type INNER JOIN type_not_effective ON type.fId = type_not_effective.notEffectiveId " +
            "WHERE type_not_effective.typeId = :typeId")
    List<Type> getNotEffectiveTypes(long typeId);

    @Query("SELECT type.fId FROM type INNER JOIN type_not_effective ON type.fId = type_not_effective.notEffectiveId " +
            "WHERE type_not_effective.typeId = :typeId")
    List<Long> getNotEffectiveTypesIds(long typeId);

    @Query("SELECT COUNT(*) FROM move_type;")
    Long getNbOfElements();

}
