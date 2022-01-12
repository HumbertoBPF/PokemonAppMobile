package com.example.pokemonapp.activities.game.score;

import android.os.Bundle;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.ScoreAdapter;
import com.example.pokemonapp.dao.ScoreDAO;
import com.example.pokemonapp.entities.Score;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ScoreHistoryDatabaseActivity extends DatabaseNavigationActivity {

    private ScoreDAO scoreDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = getString(R.string.title_appbar_scores);
        detailsActivity = ScoreDetailsActivity.class;
        scoreDAO = PokemonAppDatabase.getInstance(this).getScoreDAO();
        super.onCreate(savedInstanceState);
        noDataTextView.setText(R.string.no_score_saved);
    }

    @Override
    protected List<Object> getResourcesFromLocal() {
        List<Object> objects = new ArrayList<>();
        objects.addAll(scoreDAO.getAllScores());
        return objects;
    }

    @Override
    protected ScoreAdapter getAdapter(List<Object> objects) {
        return new ScoreAdapter(this, objects, new ScoreAdapter.OnClickListener() {
            @Override
            public void onClick(Score score) {
                startActivity(showDetails(score));
            }
        });
    }
}