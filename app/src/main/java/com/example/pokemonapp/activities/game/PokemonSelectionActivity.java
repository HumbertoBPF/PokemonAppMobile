package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonSelectionActivity extends SelectionActivity {

    private Integer id = 0;
    private PokemonDAO pokemonDAO;
    private List<Pokemon> allPokemonList;
    private List<Pokemon> playerPokemonList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        titleAppbar = "Pokémon selection";
        nextActivityButtonText = "Go to move selection";
        nextActivity = MovesSelectionActivity.class;

        super.onCreate(savedInstanceState);

        if (gameMode.equals(getResources().getString(R.string.label_random_mode))){
            loadingDialog.show();
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    allPokemonList = pokemonDAO.getPokemonFromLocal();
                    return null;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    saveRandomTeam(getResources().getString(R.string.filename_json_player_team));
                    saveRandomTeam(getResources().getString(R.string.filename_json_cpu_team));
                    configureRecyclerView(playerRecyclerView, getResources().getString(R.string.filename_json_player_team));
                    configureRecyclerView(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                    configureNextActivityButton();
                    dismissDialogWhenViewIsDrawn(cpuRecyclerView, loadingDialog);
                }
            }).execute();
        }else if (gameMode.equals(getResources().getString(R.string.label_favorite_team_mode))){

            playerTeamLabel.setText("Choose 6 pokémon for your team : ");
            cpuTeamLabel.setVisibility(View.GONE);

            loadingDialog.show();
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    allPokemonList = pokemonDAO.getPokemonFromLocal();
                    List<Object> objects = new ArrayList<>();
                    objects.addAll(allPokemonList);
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    saveRandomTeam(getResources().getString(R.string.filename_json_cpu_team));
                    playerRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                            objects,
                            new PokemonAdapter.OnClickListener() {
                                @Override
                                public void onClick(View view, Pokemon pokemon) {
                                    if (playerPokemonList.contains(pokemon)){
                                        ((CardView) view).setCardBackgroundColor(getResources().getColor(R.color.white));
                                        playerPokemonList.remove(pokemon);
                                    }else{
                                        if (playerPokemonList.size() >= 6){
                                            Toast.makeText(getApplicationContext(), "You cannot choose more than 6 pokémon", Toast.LENGTH_LONG).show();
                                        }else{
                                            ((CardView) view).setCardBackgroundColor(getResources().getColor(R.color.selection_gray));
                                            playerPokemonList.add(pokemon);
                                        }
                                    }
                                }
                            }));
                    configureConfirmChoiceButton();
                    dismissDialogWhenViewIsDrawn(playerRecyclerView, loadingDialog);
                }
            }).execute();
        }
    }

    private void configureConfirmChoiceButton(){
        nextActivityButton.setText("Done");
        nextActivityButton.setVisibility(View.VISIBLE);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerPokemonList.size() == 6){
                    List<InGamePokemon> team = new ArrayList<>();
                    for (Pokemon pokemon : playerPokemonList){
                        team.add(new InGamePokemon(id, pokemon));
                        id++;
                    }
                    saveTeam(getApplicationContext(), getResources().getString(R.string.filename_json_player_team), team);

                    playerTeamLabel.setText(getResources().getString(R.string.player_team_label));
                    cpuTeamLabel.setVisibility(View.VISIBLE);
                    configureRecyclerView(playerRecyclerView, getResources().getString(R.string.filename_json_player_team));
                    configureRecyclerView(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                    rootScrollView.smoothScrollTo(0,0);
                    configureNextActivityButton();
                }else{
                    Toast.makeText(getApplicationContext(),"You have to choose 6 pokémon for your team",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void configureRecyclerView(RecyclerView recyclerView, String filenameJson) {
        List<Object> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : loadTeam(getApplicationContext(), filenameJson)){
            pokemonList.add(inGamePokemon.getPokemonServer());
        }
        recyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                pokemonList,
                new PokemonAdapter.OnClickListener() {
                    @Override
                    public void onClick(View view, Pokemon pokemon) {

                    }
        }));
    }

    private void saveRandomTeam(String key){
        List<Integer> indexes = getDistinctRandomIntegers(0, allPokemonList.size()-1,6);
        List<InGamePokemon> team = new ArrayList<>();
        for (Integer index : indexes){
            team.add(new InGamePokemon(id, allPokemonList.get(index)));
            id++;
        }
        saveTeam(getApplicationContext(), key, team);
    }

}