package com.example.pokemonapp.activities.databases_navigation.types;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.TypesAdapter;
import com.example.pokemonapp.dao.TypeDAO;
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
                new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        startActivity(showDetails(object));
                    }
                });
    }
}