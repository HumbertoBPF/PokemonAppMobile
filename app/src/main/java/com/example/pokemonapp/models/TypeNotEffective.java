package com.example.pokemonapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "type_not_effective",
        primaryKeys = { "typeId", "notEffectiveId" },
        foreignKeys = {
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "typeId"),
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "notEffectiveId")
        })
public class TypeNotEffective {

    @NonNull
    private Long typeId;
    @NonNull
    private Long notEffectiveId;

    public TypeNotEffective(@NonNull Long typeId, @NonNull Long notEffectiveId) {
        this.typeId = typeId;
        this.notEffectiveId = notEffectiveId;
    }

    @NonNull
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(@NonNull Long typeId) {
        this.typeId = typeId;
    }

    @NonNull
    public Long getNotEffectiveId() {
        return notEffectiveId;
    }

    public void setNotEffectiveId(@NonNull Long notEffectiveId) {
        this.notEffectiveId = notEffectiveId;
    }
}
