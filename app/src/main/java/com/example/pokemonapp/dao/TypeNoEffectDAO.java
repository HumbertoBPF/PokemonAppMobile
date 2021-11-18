package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.models.Type;
import com.example.pokemonapp.models.TypeNoEffect;

import java.util.List;

@Dao
public interface TypeNoEffectDAO {

    @Insert(onConflict = REPLACE)
    void save(List<TypeNoEffect> typeNoEffects);

    @Query("SELECT * FROM type INNER JOIN type_no_effect ON type.fId = type_no_effect.noEffectId " +
            "WHERE type_no_effect.typeId = :typeId")
    List<Type> getNoEffectTypes(long typeId);

}
