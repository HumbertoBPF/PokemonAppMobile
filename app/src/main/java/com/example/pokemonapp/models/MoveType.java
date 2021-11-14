package com.example.pokemonapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(tableName = "move_type",
        primaryKeys = { "moveId", "typeId" },
        foreignKeys = {
                @ForeignKey(entity = Move.class,
                        parentColumns = "fId",
                        childColumns = "moveId"),
                @ForeignKey(entity = Type.class,
                        parentColumns = "fId",
                        childColumns = "typeId")
})
public class MoveType {

    @NonNull
    private Long moveId;
    @NonNull
    private Long typeId;

    public MoveType(@NonNull Long moveId, @NonNull Long typeId) {
        this.moveId = moveId;
        this.typeId = typeId;
    }

    @NonNull
    public Long getMoveId() {
        return moveId;
    }

    public void setMoveId(@NonNull Long moveId) {
        this.moveId = moveId;
    }

    @NonNull
    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(@NonNull Long typeId) {
        this.typeId = typeId;
    }
}
