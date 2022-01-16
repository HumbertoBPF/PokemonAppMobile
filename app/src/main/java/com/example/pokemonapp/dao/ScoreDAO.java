package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Score;

@Dao
public abstract class ScoreDAO extends LocalDAO<Score>{

    public ScoreDAO() {
        super("Score");
    }

    @Query("SELECT max(scoreValue) FROM Score")
    public abstract Long getMaxScore();

}
