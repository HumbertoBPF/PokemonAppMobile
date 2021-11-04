package com.example.pokemonapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Type {

    @PrimaryKey(autoGenerate = true)
    private Long fId;
    private String fName;
//    @ManyToMany
//    private List<Type> effective = new ArrayList<>();
//    @ManyToMany
//    private List<Type> notEffective = new ArrayList<>();
//    @ManyToMany
//    private List<Type> noEffect = new ArrayList<>();

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
