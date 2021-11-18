package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.pokemonapp.models.TypeEffective;

import java.util.List;

@Dao
public interface TypeEffectiveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<TypeEffective> typeEffectives);

}
