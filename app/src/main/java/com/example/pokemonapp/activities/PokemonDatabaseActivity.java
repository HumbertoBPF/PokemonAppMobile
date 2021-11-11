package com.example.pokemonapp.activities;

import android.os.Bundle;

import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.models.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonDatabaseActivity extends DatabaseNavigationActivity {

    private PokemonDAO pokemonDAO;
    private List<Pokemon> pokemons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();

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
                recyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),pokemons));
            }
        }).execute();

    }
}