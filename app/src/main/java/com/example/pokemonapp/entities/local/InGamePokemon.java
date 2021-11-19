package com.example.pokemonapp.entities.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class InGamePokemon {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private Long idServer;
    private Integer attack;
    private Integer defense;
    private Integer spAttack;
    private Integer spDefense;
    private Integer speed;
    private Integer hp;
    private Integer accuracy = 100;
    private Integer evasion = 0;
    // add relationship with the 4 moves from the server

    public InGamePokemon(Integer attack, Integer defense, Integer spAttack, Integer spDefense, Integer speed, Integer hp, Integer accuracy, Integer evasion) {
        this.attack = attack;
        this.defense = defense;
        this.spAttack = spAttack;
        this.spDefense = spDefense;
        this.speed = speed;
        this.hp = hp;
        this.accuracy = accuracy;
        this.evasion = evasion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdServer() {
        return idServer;
    }

    public void setIdServer(Long idServer) {
        this.idServer = idServer;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }

    public Integer getSpAttack() {
        return spAttack;
    }

    public void setSpAttack(Integer spAttack) {
        this.spAttack = spAttack;
    }

    public Integer getSpDefense() {
        return spDefense;
    }

    public void setSpDefense(Integer spDefense) {
        this.spDefense = spDefense;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Integer accuracy) {
        this.accuracy = accuracy;
    }

    public Integer getEvasion() {
        return evasion;
    }

    public void setEvasion(Integer evasion) {
        this.evasion = evasion;
    }
}
