package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.PokemonType;
import com.example.pokemonapp.entities.Type;

import java.util.List;

@Dao
public interface PokemonTypeDAO extends PokemonAppDAO<PokemonType>{

    @Query("SELECT * FROM pokemon_type")
    List<PokemonType> getAllPokemonTypeRelationshipsFromLocal();

    @Query("SELECT * FROM type INNER JOIN pokemon_type ON type.fId = pokemon_type.typeId WHERE pokemon_type.pokemonId = :pokemonId")
    List<Type> getTypesOfPokemon(long pokemonId);

    @Query("SELECT type.fId FROM type INNER JOIN pokemon_type ON type.fId = pokemon_type.typeId WHERE pokemon_type.pokemonId = :pokemonId")
    List<Long> getTypesOfPokemonIds(long pokemonId);

    @Query("SELECT COUNT(*) FROM pokemon_type WHERE pokemon_type.typeId = :typeId")
    Integer getNbPokemonWithThisType(long typeId);

}
