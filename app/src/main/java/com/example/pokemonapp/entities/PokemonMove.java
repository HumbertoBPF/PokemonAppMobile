package com.example.pokemonapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "pokemon_moves",
        primaryKeys = { "pokemonId", "moveId" },
        foreignKeys = {
                @ForeignKey(entity = Pokemon.class,
                        parentColumns = "fId",
                        childColumns = "pokemonId"),
                @ForeignKey(entity = Move.class,
                        parentColumns = "fId",
                        childColumns = "moveId")
        })
public class PokemonMove {

    @NonNull
    private Long pokemonId;
    @NonNull
    private Long moveId;

    public PokemonMove(@NonNull Long pokemonId, @NonNull Long moveId) {
        this.pokemonId = pokemonId;
        this.moveId = moveId;
    }

    @NonNull
    public Long getPokemonId() {
        return pokemonId;
    }

    @NonNull
    public Long getMoveId() {
        return moveId;
    }
}
