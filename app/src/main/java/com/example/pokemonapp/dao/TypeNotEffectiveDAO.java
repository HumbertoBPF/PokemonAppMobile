package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.pokemonapp.models.TypeNotEffective;

import java.util.List;

@Dao
public interface TypeNotEffectiveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<TypeNotEffective> typeNotEffectives);

}
