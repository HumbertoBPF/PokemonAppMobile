package com.example.pokemonapp.activities.databases_navigation;

import static com.example.pokemonapp.util.Tools.setAppbarColor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.pokemonapp.R;

public abstract class DatabaseDetailsActivity extends AppCompatActivity {

    protected int colorAppbar;
    protected String titleAppbar;
    protected int layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        configureAppbar();
    }

    private void configureAppbar() {
        setAppbarColor(this, colorAppbar);
        setTitle(titleAppbar);
    }

    protected abstract void getLayoutElements();

    protected abstract void bind();

}