package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.pokemonapp.models.TypeNoEffect;

import java.util.List;

@Dao
public interface TypeNoEffectDAO {

    @Insert(onConflict = REPLACE)
    void save(List<TypeNoEffect> typeNoEffects);

}
