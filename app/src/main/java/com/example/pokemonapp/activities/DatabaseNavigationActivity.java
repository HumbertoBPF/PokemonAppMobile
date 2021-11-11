package com.example.pokemonapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.pokemonapp.R;

public class DatabaseNavigationActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_navigation);

        recyclerView = findViewById(R.id.pokemon_recycler_view);
    }
}