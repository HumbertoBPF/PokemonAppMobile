package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.PokemonMove;

import java.util.List;

@Dao
public interface PokemonMoveDAO extends PokemonAppDAO<PokemonMove>{

    @Query("SELECT * FROM move INNER JOIN pokemon_moves ON move.fId = pokemon_moves.moveId" +
            " WHERE pokemon_moves.pokemonId = :pokemonId")
    List<Move> getMovesOfPokemon(long pokemonId);

    @Query("SELECT * FROM move INNER JOIN pokemon_moves ON move.fId = pokemon_moves.moveId" +
            " WHERE (pokemon_moves.pokemonId = :pokemonId AND move.fUserFaints = 0) ORDER BY move.fPower DESC LIMIT 4")
    List<Move> getBestMovesOfPokemon(long pokemonId);

    @Query("SELECT COUNT(*) FROM pokemon_moves;")
    Long getNbOfElements();

}
