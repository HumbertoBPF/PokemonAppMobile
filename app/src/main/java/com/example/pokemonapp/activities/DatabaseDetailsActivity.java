package com.example.pokemonapp.activities;

import static com.example.pokemonapp.util.UiTools.setAppbarColor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public abstract class DatabaseDetailsActivity extends AppCompatActivity {

    protected int colorAppbar;
    protected String titleAppbar;
    protected int layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        configureAppbar();
        getLayoutElements();
        bind();
    }

    private void configureAppbar() {
        setAppbarColor(this, colorAppbar);
        setTitle(titleAppbar);
    }

    protected abstract void getLayoutElements();

    protected abstract void bind();

}