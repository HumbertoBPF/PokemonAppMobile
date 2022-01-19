package com.example.pokemonapp.activities.databases_navigation.types;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.TypesAdapter;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.List;

public class TypesDatabaseActivity extends DatabaseNavigationActivity<Type> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.types_theme_color);
        titleAppbar = getString(R.string.title_appbar_types_db);
        detailsActivity = TypesDetailsActivity.class;
        baseDAO = PokemonAppDatabase.getInstance(this).getTypeDAO();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List<Type> types) {
        return new TypesAdapter(getApplicationContext(), types,
                new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        startActivity(showDetails(object));
                    }
                });
    }
}