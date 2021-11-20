package com.example.pokemonapp.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "type_no_effect",
        primaryKeys = { "typeId", "noEffectId" },
        foreignKeys = {
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "typeId"),
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "noEffectId")
        })
public class TypeNoEffect {

    @NonNull
    private Long typeId;
    @NonNull
    private Long noEffectId;

    public TypeNoEffect(@NonNull Long typeId, @NonNull Long noEffectId) {
        this.typeId = typeId;
        this.noEffectId = noEffectId;
    }

    @NonNull
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(@NonNull Long typeId) {
        this.typeId = typeId;
    }

    @NonNull
    public Long getNoEffectId() {
        return noEffectId;
    }

    public void setNoEffectId(@NonNull Long noEffectId) {
        this.noEffectId = noEffectId;
    }
}
