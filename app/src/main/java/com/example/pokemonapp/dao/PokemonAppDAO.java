package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

@Dao
public interface PokemonAppDAO<E> {

    @Insert(onConflict = REPLACE)
    void save(List<E> moves);

}
