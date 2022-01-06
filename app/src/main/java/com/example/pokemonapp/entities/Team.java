package com.example.pokemonapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Team {

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
