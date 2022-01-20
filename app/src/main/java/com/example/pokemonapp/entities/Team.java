package com.example.pokemonapp.entities;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pokemonapp.models.InGamePokemon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Team implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private String team;

    public Team() {
    }

    public Team(Integer id, String name, String team) {
        this.id = id;
        this.name = name;
        this.team = team;
    }

    /**
     * Entity used to save a pokémon team. A pokémon team consists of a list of 6 pokémon.
     * @param name name of the team. It is simply a String used to identify the team.
     * @param team the list of 6 InGamePokémon converted into a JSON.
     */
    public Team(String name, String team) {
        this.name = name;
        this.team = team;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    /**
     * Converts the team attribute of a Team object (it is a JSON String) into a list of pokémon.
     * @return a list of the pokémon corresponding to team attribute of the specified object.
     */
    @Nullable
    public List<InGamePokemon> getInGamePokemonFromTeam() {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<ArrayList<InGamePokemon>>() {}.getType();
        return gson.fromJson(team, type);
    }

    /**
     * @return sum of the Overall Points of the pokémon of the specified team.
     */
    public int getOverallPointsOfTeam() {
        int teamOverallPoints = 0;
        for (InGamePokemon inGamePokemon : getInGamePokemonFromTeam()){
            teamOverallPoints += inGamePokemon.getPokemonServer().getFOverallPts();
        }
        return teamOverallPoints;
    }

}
