package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.models.PokemonType;
import com.example.pokemonapp.models.Type;

import java.util.List;

@Dao
public interface PokemonTypeDAO {

    @Insert(onConflict = REPLACE)
    void save(List<PokemonType> pokemonTypes);

    @Query("SELECT * FROM pokemon_type")
    List<PokemonType> getAllPokemonTypeRelationshipsFromLocal();

    @Query("SELECT * FROM type INNER JOIN pokemon_type ON type.fId = pokemon_type.typeId WHERE pokemon_type.pokemonId = :pokemonId")
    List<Type> getTypesOfPokemon(long pokemonId);

    @Query("SELECT COUNT(pokemon_type.pokemonId) FROM pokemon_type WHERE pokemon_type.typeId = :typeId")
    Integer getPokemonWithThisType(long typeId);

}
