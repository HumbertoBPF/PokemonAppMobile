package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.entities.TypeNoEffect;

import java.util.List;

@Dao
public interface TypeNoEffectDAO {

    @Insert(onConflict = REPLACE)
    void save(List<TypeNoEffect> typeNoEffects);

    @Query("SELECT * FROM type INNER JOIN type_no_effect ON type.fId = type_no_effect.noEffectId " +
            "WHERE type_no_effect.typeId = :typeId")
    List<Type> getNoEffectTypes(long typeId);

    @Query("SELECT type.fId FROM type INNER JOIN type_no_effect ON type.fId = type_no_effect.noEffectId " +
            "WHERE type_no_effect.typeId = :typeId")
    List<Long> getNoEffectTypesIds(long typeId);

    @Query("SELECT COUNT(*) FROM move_type;")
    Long getNbOfElements();

}
