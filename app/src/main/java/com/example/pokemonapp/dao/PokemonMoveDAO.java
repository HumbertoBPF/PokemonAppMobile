package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;

import com.example.pokemonapp.entities.PokemonMove;

import java.util.List;

@Dao
public interface PokemonMoveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<PokemonMove> pokemonMoves);

}
