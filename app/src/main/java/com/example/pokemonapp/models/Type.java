package com.example.pokemonapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Type implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long fId;
    private String fName;

    public Type() {
    }

    public Type(String fName) {
        this.fName = fName;
    }

    public Type(Long fId, String fName) {
        this.fId = fId;
        this.fName = fName;
    }

    public Long getFId() {
        return fId;
    }

    public void setFId(long fId) {
        this.fId = fId;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

}
