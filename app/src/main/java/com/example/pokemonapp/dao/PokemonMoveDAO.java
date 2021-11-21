package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.PokemonMove;

import java.util.List;

@Dao
public interface PokemonMoveDAO {

    @Insert(onConflict = REPLACE)
    void save(List<PokemonMove> pokemonMoves);

    @Query("SELECT * FROM move INNER JOIN pokemon_moves ON move.fId = pokemon_moves.moveId" +
            " WHERE pokemon_moves.pokemonId = :pokemonId")
    List<Move> getMovesOfPokemon(long pokemonId);

}
