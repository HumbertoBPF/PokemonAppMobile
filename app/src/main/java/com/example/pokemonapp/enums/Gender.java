package com.example.pokemonapp.enums;

public enum Gender {

    MALE("Male"),
    FEMALE("Female"),
    BOTH("Both"),
    UNKNOWN("Unknown");

    private String value;

    private Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
