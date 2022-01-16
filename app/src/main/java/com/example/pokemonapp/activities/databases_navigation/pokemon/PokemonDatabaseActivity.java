package com.example.pokemonapp.activities.databases_navigation.pokemon;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonDatabaseActivity extends DatabaseNavigationActivity {

    private PokemonDAO pokemonDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getString(R.string.title_appbar_pokemon_db);
        detailsActivity = PokemonDetailsActivity.class;
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        super.onCreate(savedInstanceState);
    }

    protected List<Object> getResourcesFromLocal() {
        List<Object> objects = new ArrayList<>();
        objects.addAll(pokemonDAO.getPokemonFromLocal());
        return objects;
    }

    @NonNull
    protected PokemonAdapter getAdapter(List<Object> objects) {
        return new PokemonAdapter(getApplicationContext(), objects,
                new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        startActivity(showDetails(object));
                    }
                });
    }
}