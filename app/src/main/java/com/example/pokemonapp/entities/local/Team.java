package com.example.pokemonapp.entities.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.pokemonapp.enums.Owner;

@Entity
public class Team {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Owner owner;
    // add relationship with 6 InGamePokemon entities

    public Team(Owner owner) {
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }
}
