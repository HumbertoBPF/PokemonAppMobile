package com.example.pokemonapp.activities.game.team;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.databases_navigation.DatabaseNavigationActivity;
import com.example.pokemonapp.adapters.TeamAdapter;
import com.example.pokemonapp.dao.TeamDAO;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TeamDatabaseActivity extends DatabaseNavigationActivity {

    private TeamDAO teamDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        colorAppbar = getResources().getColor(R.color.pokemon_theme_color);
        titleAppbar = "My teams";
        detailsActivity = TeamDetailsActivity.class;
        teamDAO = PokemonAppDatabase.getInstance(this).getTeamDAO();
        super.onCreate(savedInstanceState);
        noDataTextView.setText(R.string.no_team_saved);
    }

    @Override
    protected List<Object> getResourcesFromLocal() {
        List<Object> objects = new ArrayList<>();
        objects.addAll(teamDAO.getAllTeams());
        return objects;
    }

    @Override
    protected TeamAdapter getAdapter(List<Object> objects) {
        return new TeamAdapter(this, objects, new TeamAdapter.OnClickListener() {
            @Override
            public void onClick(Team team) {
                Intent intent = showDetails(team);
                intent.putExtra("hideButton",true);
                startActivity(intent);
            }
        },true);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        configureRecyclerView();    // reconfigure the recycler view since some item may have been edited
    }
}