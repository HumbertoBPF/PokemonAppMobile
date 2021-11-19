package com.example.pokemonapp.entities.server_side;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "type_effective",
        primaryKeys = { "typeId", "effectiveId" },
        foreignKeys = {
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "typeId"),
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "effectiveId")
        })
public class TypeEffective {

    @NonNull
    private Long typeId;
    @NonNull
    private Long effectiveId;

    public TypeEffective(@NonNull Long typeId, @NonNull Long effectiveId) {
        this.typeId = typeId;
        this.effectiveId = effectiveId;
    }

    @NonNull
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(@NonNull Long typeId) {
        this.typeId = typeId;
    }

    @NonNull
    public Long getEffectiveId() {
        return effectiveId;
    }

    public void setEffectiveId(@NonNull Long effectiveId) {
        this.effectiveId = effectiveId;
    }
}
