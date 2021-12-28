package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.Tools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.makeSelector;
import static com.example.pokemonapp.util.Tools.saveTeam;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
        nextActivity = MovesSelectionActivity.class;

        super.onCreate(savedInstanceState);

        if (gameMode.equals(getResources().getString(R.string.label_random_mode))){
            loadingDialog.show();
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    return getAllPokemonFromLocalDB();
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    saveRandomTeam(getResources().getString(R.string.filename_json_player_team));
                    saveRandomTeam(getResources().getString(R.string.filename_json_cpu_team));
                    configureRecyclerView(playerRecyclerView, getResources().getString(R.string.filename_json_player_team));
                    configureRecyclerView(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                    configureNextActivityButton("Go to move selection");
                    dismissDialogWhenViewIsDrawn(cpuRecyclerView, loadingDialog);   // when both teams have been chosen, the loading
                                                                                    // dialog is dismissed
                }
            }).execute();
        }else if (gameMode.equals(getResources().getString(R.string.label_favorite_team_mode))){

            instructionTextView.setVisibility(View.VISIBLE);
            instructionTextView.setText("Choose 6 pokémon for your team : ");
            playerTeamLabel.setVisibility(View.GONE);
            cpuTeamLabel.setVisibility(View.GONE);

            loadingDialog.show();
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    return getAllPokemonFromLocalDB();
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    saveRandomTeam(getResources().getString(R.string.filename_json_cpu_team));  // save a random team for the CPU
                    // shows all the pokémon for the players so as he can pick 6 for their team
                    playerRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                            objects,
                            new PokemonAdapter.OnClickListener() {
                                @Override
                                public void onClick(View view, Pokemon pokemon) {
                                    selectItemRecyclerView((CardView) view, pokemon);
                                    updateConfirmButtonColor();
                                }
                            }));
                    configureConfirmChoiceButton();
                    dismissDialogWhenViewIsDrawn(playerRecyclerView, loadingDialog);    // when all has been set up, dismiss loading dialog
                }
            }).execute();
        }
    }

    @NonNull
    private List<Object> getAllPokemonFromLocalDB() {
        allPokemonList = pokemonDAO.getPokemonFromLocal();
        List<Object> objects = new ArrayList<>();
        objects.addAll(allPokemonList);
        return objects;
    }

    /**
     * Updates the color of the confirm button according to whether player's pokémon choice was already
     * been finished. It gives a feedback to the users about whether they can go to the next step.
     */
    private void updateConfirmButtonColor() {
        if (playerPokemonList.size() == 6){
            nextActivityButton.setBackgroundColor(getResources().getColor(R.color.red));
        }else{
            nextActivityButton.setBackgroundColor(getResources().getColor(R.color.button_disabled_color));
        }
    }

    /**
     * Selects an item of the pokémon RecyclerView, i.e. darkens the item to indicate it it is
     * selected and adds/removes it from the list of chosen pokémon according to its previous state
     * (if it was in the list, removes it and vice-versa).
     * @param view view of the item of the list corresponding to the selected pokémon.
     * @param pokemon pokémon object selected.
     */
    private void selectItemRecyclerView(CardView view, Pokemon pokemon) {
        if (playerPokemonList.contains(pokemon)){   // if the pokémon had already been selected, it is deselected, i.e. the background
                                                    // color is set to white and it is removed from the list
            view.setBackground(makeSelector(getResources().getColor(R.color.white),0.8f));
            playerPokemonList.remove(pokemon);
        }else{                                      // otherwise, it is verified if 6 pokémon were already chosen. If not, the pokémon
                                                    // view is marked as selected and added to the list.
            if (playerPokemonList.size() >= 6){
                Toast.makeText(getApplicationContext(), "You cannot choose more than 6 pokémon", Toast.LENGTH_LONG).show();
            }else{
                view.setBackground(makeSelector(getResources().getColor(R.color.selection_gray),1.0f));
                playerPokemonList.add(pokemon);
            }
        }
    }

    /**
     * Configuration of the button when the players can use it to confirm their team choice. If the
     * players have already chosen 6 pokémon, their team and the CPU's one is shown, otherwise a
     * toast is shown asking the user to choose 6 pokémon.
     */
    private void configureConfirmChoiceButton(){
        nextActivityButton.setText("Done");
        nextActivityButton.setVisibility(View.VISIBLE);
        nextActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (playerPokemonList.size() == 6){ // if the players have choosen 6 pokémon, save their team in Shared Preferences
                                                    // and show their team and the CPU's one
                    List<InGamePokemon> team = new ArrayList<>();
                    for (Pokemon pokemon : playerPokemonList){
                        team.add(new InGamePokemon(id, pokemon));
                        id++;
                    }
                    saveTeam(getApplicationContext(), getResources().getString(R.string.filename_json_player_team), team);

                    instructionTextView.setVisibility(View.GONE);
                    playerTeamLabel.setVisibility(View.VISIBLE);
                    cpuTeamLabel.setVisibility(View.VISIBLE);

                    configureRecyclerView(playerRecyclerView, getResources().getString(R.string.filename_json_player_team));
                    configureRecyclerView(cpuRecyclerView, getResources().getString(R.string.filename_json_cpu_team));
                    rootScrollView.smoothScrollTo(0,0);
                    configureNextActivityButton("Go to move selection");
                }else{                              // otherwise, show toast giving instructions to the player
                    Toast.makeText(getApplicationContext(),"You have to choose 6 pokémon for your team",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Shows the pokémon of a team stored in Shared Preferences into a RecyclerView.
     * @param recyclerView RecyclerView to be populated with the pokémon of the team.
     * @param key key corresponding to the team saved in Shared Preferences.
     */
    private void configureRecyclerView(RecyclerView recyclerView, String key) {
        // get list of pokémon from Shared Preferences
        List<Object> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : loadTeam(getApplicationContext(), key)){
            pokemonList.add(inGamePokemon.getPokemonServer());
        }
        // loads this list into the RecyclerView
        recyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                pokemonList,
                new PokemonAdapter.OnClickListener() {
                    @Override
                    public void onClick(View view, Pokemon pokemon) {

                    }
        }));
    }

    /**
     * Picks a random group of 6 distinct pokémon and saves it in Shared Preferences.
     * @param key the key that will be used to store the team in Shared Preferences.
     */
    private void saveRandomTeam(String key){
        List<Integer> indexes = getDistinctRandomIntegers(0, allPokemonList.size()-1,6);    // get 6 random pokémon
        List<InGamePokemon> team = new ArrayList<>();
        for (Integer index : indexes){  // put in a list
            team.add(new InGamePokemon(id, allPokemonList.get(index)));
            id++;
        }
        saveTeam(getApplicationContext(), key, team);   // saves in Shared Preferences
    }

}