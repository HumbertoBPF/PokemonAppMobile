package com.example.pokemonapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

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
}
