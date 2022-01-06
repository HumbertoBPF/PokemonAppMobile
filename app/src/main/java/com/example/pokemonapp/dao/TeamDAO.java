package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.Team;

import java.util.List;

@Dao
public interface TeamDAO extends PokemonAppDAO<Team>{

    @Query("SELECT name FROM Team;")
    List<String> getAllTeamNames();

    @Insert(onConflict = REPLACE)
    void save(Team entity);

}
