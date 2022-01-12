package com.example.pokemonapp.activities.databases_navigation.types;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.TypesAdapter;
import com.example.pokemonapp.dao.TypeDAO;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TypesDatabaseActivity extends DatabaseNavigationActivity {

    private TypeDAO typeDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.types_theme_color);
        titleAppbar = getString(R.string.title_appbar_types_db);
        detailsActivity = TypesDetailsActivity.class;
        typeDAO = PokemonAppDatabase.getInstance(this).getTypeDAO();
        super.onCreate(savedInstanceState);
    }

    protected List<Object> getResourcesFromLocal() {
        List<Object> objects = new ArrayList<>();
        objects.addAll(typeDAO.getAllTypesFromLocal());
        return objects;
    }

    @NonNull
    protected TypesAdapter getAdapter(List<Object> objects) {
        return new TypesAdapter(getApplicationContext(), objects,
                new TypesAdapter.OnClickListener() {
                    @Override
                    public void onClick(Type type) {
                        startActivity(showDetails(type));
                    }
                });
    }
}