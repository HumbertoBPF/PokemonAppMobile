package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.entities.Team;

import java.util.List;

@Dao
public abstract class TeamDAO extends LocalDAO<Team>{

    public TeamDAO() {
        super("Team");
    }

    @Query("SELECT name FROM Team;")
    public abstract List<String> getAllTeamNames();

}
