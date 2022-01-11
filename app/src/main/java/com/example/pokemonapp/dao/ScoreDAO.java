package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.pokemonapp.entities.Score;

import java.util.List;

@Dao
public interface ScoreDAO extends PokemonAppDAO<Score>{

    @Insert(onConflict = REPLACE)
    void save(Score score);

    @Query("SELECT * FROM Score")
    List<Score> getAllScores();

}
