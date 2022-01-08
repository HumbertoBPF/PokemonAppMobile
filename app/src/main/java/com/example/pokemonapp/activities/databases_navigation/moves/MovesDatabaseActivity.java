package com.example.pokemonapp.activities.databases_navigation.moves;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesDatabaseActivity extends DatabaseNavigationActivity {

    private MoveDAO moveDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.moves_theme_color);
        titleAppbar = getString(R.string.title_appbar_moves_db);
        detailsActivity = MovesDetailsActivity.class;
        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        super.onCreate(savedInstanceState);
    }

    protected List<Object> getResourcesFromLocal() {
        List<Object> objects = new ArrayList<>();
        objects.addAll(moveDAO.getAllMovesFromLocal());
        return objects;
    }

    @NonNull
    protected MovesAdapter getAdapter(List<Object> objects) {
        return new MovesAdapter(getApplicationContext(), objects,
                new MovesAdapter.OnClickListener() {
                    @Override
                    public void onClick(View view, Move move) {
                        startActivity(showDetails(move));
                    }
                });
    }
}