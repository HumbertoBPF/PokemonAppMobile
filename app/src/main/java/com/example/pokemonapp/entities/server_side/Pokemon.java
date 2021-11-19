package com.example.pokemonapp.entities.server_side;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Pokemon implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long fId;
    private String fName;
    private String fDescription;
    private Double fHeight;
    private Double fWeight;
    private String fGender;
    private String fCategory;
    private Integer fAttack;
    private Integer fDefense;
    private Integer fSpAttack;
    private Integer fSpDefense;
    private Integer fSpeed;
    private Integer fHp;
    private Integer fAccuracy = 100;
    private Integer fEvasion = 0;

    public Pokemon() {
    }

    public Pokemon(Long fId, String fName, String fDescription, Double fHeight, Double fWeight, String fGender,
                   String fCategory, Integer fAttack, Integer fDefense, Integer fSpAttack, Integer fSpDefense,
                   Integer fSpeed, Integer fHp) {
        this.fId = fId;
        this.fName = fName;
        this.fDescription = fDescription;
        this.fHeight = fHeight;
        this.fWeight = fWeight;
        this.fGender = fGender;
        this.fCategory = fCategory;
        this.fAttack = fAttack;
        this.fDefense = fDefense;
        this.fSpAttack = fSpAttack;
        this.fSpDefense = fSpDefense;
        this.fSpeed = fSpeed;
        this.fHp = fHp;
    }

    public Pokemon(String fName, String fDescription, Double fHeight, Double fWeight, String fGender, String fCategory,
                   Integer fAttack, Integer fDefense, Integer fSpAttack, Integer fSpDefense, Integer fSpeed, Integer fHp) {
        this.fName = fName;
        this.fDescription = fDescription;
        this.fHeight = fHeight;
        this.fWeight = fWeight;
        this.fGender = fGender;
        this.fCategory = fCategory;
        this.fAttack = fAttack;
        this.fDefense = fDefense;
        this.fSpAttack = fSpAttack;
        this.fSpDefense = fSpDefense;
        this.fSpeed = fSpeed;
        this.fHp = fHp;
    }

    public Long getFId() {
        return fId;
    }

    public void setFId(Long fId) {
        this.fId = fId;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getFDescription() {
        return fDescription;
    }

    public void setFDescription(String fDescription) {
        this.fDescription = fDescription;
    }

    public Double getFHeight() {
        return fHeight;
    }

    public void setFHeight(Double fHeight) {
        this.fHeight = fHeight;
    }

    public Double getFWeight() {
        return fWeight;
    }

    public void setFWeight(Double fWeight) {
        this.fWeight = fWeight;
    }

    public String getFGender() {
        return fGender;
    }

    public void setFGender(String fGender) {
        this.fGender = fGender;
    }

    public String getFCategory() {
        return fCategory;
    }

    public void setFCategory(String fCategory) {
        this.fCategory = fCategory;
    }

    public Integer getFAttack() {
        return fAttack;
    }

    public void setFAttack(Integer fAttack) {
        this.fAttack = fAttack;
    }

    public Integer getFDefense() {
        return fDefense;
    }

    public void setFDefense(Integer fDefense) {
        this.fDefense = fDefense;
    }

    public Integer getFSpAttack() {
        return fSpAttack;
    }

    public void setFSpAttack(Integer fSpAttack) {
        this.fSpAttack = fSpAttack;
    }

    public Integer getFSpDefense() {
        return fSpDefense;
    }

    public void setFSpDefense(Integer fSpDefense) {
        this.fSpDefense = fSpDefense;
    }

    public Integer getFSpeed() {
        return fSpeed;
    }

    public void setFSpeed(Integer fSpeed) {
        this.fSpeed = fSpeed;
    }

    public Integer getFHp() {
        return fHp;
    }

    public void setFHp(Integer fHp) {
        this.fHp = fHp;
    }

    public Integer getFAccuracy() {
        return fAccuracy;
    }

    public void setFAccuracy(Integer fAccuracy) {
        this.fAccuracy = fAccuracy;
    }

    public Integer getFEvasion() {
        return fEvasion;
    }

    public void setFEvasion(Integer fEvasion) {
        this.fEvasion = fEvasion;
    }
}
