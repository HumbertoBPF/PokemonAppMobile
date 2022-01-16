package com.example.pokemonapp.activities.databases_navigation.pokemon;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonDatabaseActivity extends DatabaseNavigationActivity<Pokemon> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getString(R.string.title_appbar_pokemon_db);
        detailsActivity = PokemonDetailsActivity.class;
        baseDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List<Pokemon> pokemon) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(pokemon);
        return new PokemonAdapter(getApplicationContext(), objects,
                new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        startActivity(showDetails(object));
                    }
                });
    }
}