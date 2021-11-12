package com.example.pokemonapp.activities.databases_navigation;

import static com.example.pokemonapp.util.Tools.setAppbarColor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;

public abstract class DatabaseNavigationActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    protected int colorAppbar;
    protected String titleAppbar;
    protected Class detailsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_navigation);

        recyclerView = findViewById(R.id.pokemon_recycler_view);

        configureAppbar();
        configureRecyclerView();
    }

    private void configureAppbar() {
        setAppbarColor(this, colorAppbar);
        setTitle(titleAppbar);
    }

    protected abstract void configureRecyclerView();

}