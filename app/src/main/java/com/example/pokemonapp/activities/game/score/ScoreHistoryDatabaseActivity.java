package com.example.pokemonapp.activities.game.score;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.ScoreAdapter;
import com.example.pokemonapp.entities.Score;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ScoreHistoryDatabaseActivity extends DatabaseNavigationActivity<Score> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getString(R.string.title_appbar_scores);
        detailsActivity = ScoreDetailsActivity.class;
        baseDAO = PokemonAppDatabase.getInstance(this).getScoreDAO();
        super.onCreate(savedInstanceState);
        noDataTextView.setText(R.string.no_score_saved);
    }

    @Override
    protected RecyclerView.Adapter getAdapter(List<Score> scores) {
        List<Object> objects = new ArrayList<>();
        objects.addAll(scores);
        return new ScoreAdapter(this, objects, new OnItemAdapterClickListener() {
            @Override
            public void onClick(View view, Object object) {
                startActivity(showDetails(object));
            }
        });
    }
}