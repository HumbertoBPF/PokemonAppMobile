package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

@Dao
public interface PokemonDAO {

    @Insert(onConflict = REPLACE)
    void save(List<Pokemon> pokemons);

    @Query("SELECT * FROM Pokemon")
    List<Pokemon> getPokemonFromLocal();

}
