package com.example.pokemonapp.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Type implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long fId;
    private String fName;
    private String fColorCode;

    public Type() {
    }

    public Type(String fName, String fColorCode) {
        this.fName = fName;
        this.fColorCode = fColorCode;
    }

    public Type(Long fId, String fName, String fColorCode) {
        this.fId = fId;
        this.fName = fName;
        this.fColorCode = fColorCode;
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

    public String getFColorCode() {
        return fColorCode;
    }

    public void setFColorCode(String fColorCode) {
        this.fColorCode = fColorCode;
    }
}
