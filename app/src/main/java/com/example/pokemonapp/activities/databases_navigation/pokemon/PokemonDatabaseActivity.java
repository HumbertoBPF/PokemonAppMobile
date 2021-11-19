package com.example.pokemonapp.activities.databases_navigation.pokemon;

import android.content.Intent;
import android.os.Bundle;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonDatabaseActivity extends DatabaseNavigationActivity {

    private PokemonDAO pokemonDAO;
    private List<Pokemon> pokemons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getResources().getString(R.string.title_appbar_pokemon_db);
        detailsActivity = PokemonDetailsActivity.class;
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        super.onCreate(savedInstanceState);
    }

    protected void configureRecyclerView() {
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                pokemons = pokemonDAO.getPokemonFromLocal();
                List<Object> objects = new ArrayList<>();
                objects.addAll(pokemons);
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                recyclerView.setAdapter(new PokemonAdapter(getApplicationContext(), pokemons, new PokemonAdapter.OnClickListener() {
                    @Override
                    public void onClick(Pokemon pokemon) {
                        Intent intent = new Intent(getApplicationContext(),detailsActivity);
                        intent.putExtra("pokemon",pokemon);
                        startActivity(intent);
                    }
                }));
            }
        }).execute();
    }
}