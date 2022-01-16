package com.example.pokemonapp.activities.databases_navigation.moves;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesDatabaseActivity extends DatabaseNavigationActivity<Move> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.moves_theme_color);
        titleAppbar = getString(R.string.title_appbar_moves_db);
        detailsActivity = MovesDetailsActivity.class;
        baseDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List<Move> moves) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(moves);
        return new MovesAdapter(getApplicationContext(), objects,
                new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {
                        startActivity(showDetails(object));
                    }
                });
    }
}