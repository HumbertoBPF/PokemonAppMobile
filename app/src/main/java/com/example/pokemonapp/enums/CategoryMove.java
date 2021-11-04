package com.example.pokemonapp.enums;

public enum CategoryMove {

    SPECIAL("Special"),
    PHYSICAL("Physical"),
    STATUS("Status");

    private String value;

    private CategoryMove(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
