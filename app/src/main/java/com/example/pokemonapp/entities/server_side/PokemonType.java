package com.example.pokemonapp.entities.server_side;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "pokemon_type",
        primaryKeys = { "pokemonId", "typeId" },
        foreignKeys = {
                @ForeignKey(entity = Pokemon.class,
                        parentColumns = "fId",
                        childColumns = "pokemonId"),
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "typeId")
        })
public class PokemonType {

    @NonNull
    private Long pokemonId;
    @NonNull
    private Long typeId;

    public PokemonType(@NonNull Long pokemonId, @NonNull Long typeId) {
        this.pokemonId = pokemonId;
        this.typeId = typeId;
    }

    @NonNull
    public Long getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(@NonNull Long pokemonId) {
        this.pokemonId = pokemonId;
    }

    @NonNull
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(@NonNull Long typeId) {
        this.typeId = typeId;
    }

}
