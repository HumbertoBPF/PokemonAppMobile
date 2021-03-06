package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.DialogTools.dismissDialogWhenViewIsDrawn;
import static com.example.pokemonapp.util.GeneralTools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.SharedPreferencesTools.loadTeam;
import static com.example.pokemonapp.util.SharedPreferencesTools.saveTeam;
import static com.example.pokemonapp.util.UiTools.makeSelector;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.SelectionActivity;
import com.example.pokemonapp.activities.game.team.LoadTeamActivity;
import com.example.pokemonapp.adapters.OnItemAdapterClickListener;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonSelectionActivity extends SelectionActivity {

    private Integer id = 0;
    private PokemonDAO pokemonDAO;
    private List<Pokemon> allPokemonRanked;         // list with all the pokémon ranked by OverallPoints
    private int indexWeakestPokemon;                // index of the weakest pokémon of allPokemonRanked to be considered
                                                    // by the CPU when it chooses its pokémon
    private int currentOverallPoints;               // current overall points
    private int maxOverallPoints;                   // max overall points
    private List<Pokemon> playerPokemonList = new ArrayList<>();
    private boolean canLoad;

    private final int LOAD_TEAM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        nextActivity = MoveSetSelectionActivity.class;

        super.onCreate(savedInstanceState);

        if (gameMode.equals(getString(R.string.label_random_mode))){
            setTitle(R.string.summary_title_app_bar);
            enableLoading(false);   // cannot load a team for the random mode since the team must be random
            loadingDialog.show();
            pokemonDAO.getPokemonOrderedByForceTask(new OnResultListener<List<Pokemon>>() {
                @Override
                public void onResult(List<Pokemon> result) {
                    allPokemonRanked = result;                                     // get pokémon ordered by OverallPoints
                    indexWeakestPokemon = getIndexWeakestPokemon();                 // defines the top pokémon to be considered by the CPU
                    saveTeamAutomatically(getString(R.string.filename_json_player_team));
                    saveTeamAutomatically(getString(R.string.filename_json_cpu_team));
                    configureRecyclerView(playerRecyclerView, getString(R.string.filename_json_player_team));
                    configureRecyclerView(cpuRecyclerView, getString(R.string.filename_json_cpu_team));
                    configureNextActivityButton(getString(R.string.go_move_selection_button_text));
                    dismissDialogWhenViewIsDrawn(cpuRecyclerView, loadingDialog);   // when both teams have been chosen, the loading
                                                                                    // dialog is dismissed
                }
            }).execute();
        }else if (gameMode.equals(getString(R.string.label_favorite_team_mode)) ||
                gameMode.equals(getString(R.string.label_strategy_mode))){
            setTitle(R.string.choose_pokemon);
            playerTeamLabel.setVisibility(View.GONE);
            cpuTeamLabel.setVisibility(View.GONE);
            enableLoading(true);    // before confirming the choice of the 6 pokémon, can load a team
            loadingDialog.show();
            pokemonDAO.getPokemonOrderedByForceTask(new OnResultListener<List<Pokemon>>() {
                @Override
                public void onResult(List<Pokemon> result) {
                    allPokemonRanked = result;                      // get pokémon ordered by OverallPoints
                    indexWeakestPokemon = getIndexWeakestPokemon(); // defines the top pokémon to be considered by the CPU
                    pokemonDAO.getAllRecordsTask(new OnResultListener<List<Pokemon>>() {
                        @Override
                        public void onResult(List<Pokemon> result) {
                            saveTeamAutomatically(getString(R.string.filename_json_cpu_team));  // save a random team for the CPU

                            if (gameMode.equals(getString(R.string.label_strategy_mode))){
                                // at the beginning the current number of Overall Points corresponds to the maximum
                                currentOverallPoints = maxOverallPoints;
                                // show the number of remaining points on the top of the screen
                                instructionTextView.setVisibility(View.VISIBLE);
                                instructionTextView.setText(getString(R.string.remaining_overall_points)+" : "+currentOverallPoints);
                            }

                            // shows all the pokémon for the players so as he can pick 6 for their team
                            playerRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(), result,
                                    new OnItemAdapterClickListener() {
                                        @Override
                                        public void onClick(View view, Object object) {
                                            selectItemRecyclerView((CardView) view, (Pokemon) object);
                                            updateConfirmButtonColor();
                                        }
                                    }));
                            configureConfirmChoiceButton();
                            dismissDialogWhenViewIsDrawn(playerRecyclerView, loadingDialog);    // when all has been set up,
                                                                                                // dismiss loading dialog
                        }
                    }).execute();
                }
            }).execute();
        }
    }

    /**
     * Shows or hides the loading item on the appbar.
     * @param state whether the item should be shown or hidden.
     */
    private void enableLoading(boolean state) {
        canLoad = state;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_load, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem itemLoad = menu.findItem(R.id.item_load);
        itemLoad.setVisible(canLoad);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_TEAM && resultCode == RESULT_OK){   // if a team to load was previously requested and if the result
                                                                    // is OK, the user is redirected to the MoveSetSelectionActivity
            Intent intent = new Intent(this, MoveSetSelectionActivity.class);
            intent.putExtra("playerTeamReady",true);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_load){
            // launches the activity where the team to be loaded is chosen so as to get this team as
            // result
            Intent intent = new Intent(this, LoadTeamActivity.class);
            if (gameMode.equals(getString(R.string.label_strategy_mode))){
                intent.putExtra("maxOverallPoints",maxOverallPoints);
            }
            startActivityForResult(intent,LOAD_TEAM);
        }
        return super.onOptionsItemSelected(item);
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
            if (gameMode.equals(getString(R.string.label_strategy_mode))){  // OverallPoints are considered only for strategy mode
                currentOverallPoints += pokemon.getFOverallPts();
                instructionTextView.setText(getString(R.string.remaining_overall_points)+" : "+currentOverallPoints);
            }
        }else{                                              // It is verified if 6 pokémon were already chosen
            if (playerPokemonList.size() >= 6){
                Toast.makeText(getApplicationContext(), R.string.max_pokemon_warning, Toast.LENGTH_LONG).show();
            }else if (!hasEnoughOverallPoints(pokemon)) {   // It is verified if the player has enough OverallPoints
                Toast.makeText(getApplicationContext(), R.string.max_overall_points_warning, Toast.LENGTH_LONG).show();
            }else{                                          // Otherwise, the pokémon view is marked as selected and added to the list
                view.setBackground(makeSelector(getResources().getColor(R.color.selection_gray),1.0f));
                playerPokemonList.add(pokemon);
                if (gameMode.equals(getString(R.string.label_strategy_mode))){  // OverallPoints are considered only for strategy mode
                    currentOverallPoints -= pokemon.getFOverallPts();
                    instructionTextView.setText(getString(R.string.remaining_overall_points)+" : "+currentOverallPoints);
                }
            }
        }
    }

    /**
     * Verifies if the players have enough OverallPoints to add the specified pokémon to their team.
     * @param pokemon pokémon to be added.
     * @return a boolean indicating if there are enough OverallPoints to spend on the specified
     * pokémon.
     */
    private boolean hasEnoughOverallPoints(Pokemon pokemon) {
        return (currentOverallPoints - pokemon.getFOverallPts() > 0) || // either the player needs to have enough OverallPoints
                !gameMode.equals(getString(R.string.label_strategy_mode));  // or the mode needs to be different from strategy mode
    }

    /**
     * Configuration of the button when the players can use it to confirm their team choice. If the
     * players have already chosen 6 pokémon, their team and the CPU's one is shown, otherwise a
     * toast is shown asking the user to choose 6 pokémon.
     */
    private void configureConfirmChoiceButton(){
        nextActivityButton.setText(getString(R.string.done_button_text));
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
                    saveTeam(getApplicationContext(), getString(R.string.filename_json_player_team), team);

                    setTitle(getString(R.string.summary_title_app_bar));
                    instructionTextView.setVisibility(View.GONE);
                    playerTeamLabel.setVisibility(View.VISIBLE);
                    cpuTeamLabel.setVisibility(View.VISIBLE);
                    enableLoading(false);   // cannot load a team after choosing the 6 pokémon

                    configureRecyclerView(playerRecyclerView, getString(R.string.filename_json_player_team));
                    configureRecyclerView(cpuRecyclerView, getString(R.string.filename_json_cpu_team));
                    rootScrollView.smoothScrollTo(0,0);
                    configureNextActivityButton(getString(R.string.go_move_selection_button_text));
                }else{                              // otherwise, show toast giving instructions to the player
                    Toast.makeText(getApplicationContext(), R.string.min_pokemon_warning,Toast.LENGTH_LONG).show();
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
        List<Pokemon> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : loadTeam(getApplicationContext(), key)){
            pokemonList.add(inGamePokemon.getPokemonServer());
        }
        // loads this list into the RecyclerView
        recyclerView.setAdapter(new PokemonAdapter(getApplicationContext(),
                pokemonList,
                new OnItemAdapterClickListener() {
                    @Override
                    public void onClick(View view, Object object) {

                    }
        }));
    }

    /**
     * Picks randomly 6 distinct pokémon among a group that depends on the game mode and on the level
     * and saves it in Shared Preferences.
     * @param key the key that will be used to store the team in Shared Preferences.
     */
    private void saveTeamAutomatically(String key){
        List<Integer> indexes = getDistinctRandomIntegers(0,indexWeakestPokemon,6);    // get 6 random pokémon
        List<InGamePokemon> team = new ArrayList<>();
        for (Integer index : indexes){  // put in a list
            team.add(new InGamePokemon(id, allPokemonRanked.get(index)));
            if (gameMode.equals(getString(R.string.label_strategy_mode))){  // there is maximum for the OverallPoints only for strategy mode
                maxOverallPoints += allPokemonRanked.get(index).getFOverallPts();
            }
            id++;
        }
        saveTeam(getApplicationContext(), key, team);   // saves in Shared Preferences
    }

    /**
     * When the game mode is favorite team and level is equal to intermediate or hard, the number of
     * pokémon to be considered by the CPU to build its team is reduced so as to choose the strongest
     * pokémon. In the intermediate level, the CPU chooses among the 50% strongest pokémon. On the
     * other hand, when in the hard level, the CPU chooses among the 33% strongest pokémon.
     * @return the number of top pokémon to be considered.
     */
    private int getIndexWeakestPokemon() {
        int nbPokemon = allPokemonRanked.size();
        if (gameMode.equals(getString(R.string.label_favorite_team_mode)) ||
                gameMode.equals(getString(R.string.label_strategy_mode))){ //
            if (gameLevel.equals(getString(R.string.hard_level))){
                Log.i("getIndexWeakestPokemon",""+(nbPokemon/3 - 1));
                return (nbPokemon/3 - 1);
            }else if (gameLevel.equals(getString(R.string.intermediate_level))){
                Log.i("getIndexWeakestPokemon",""+(nbPokemon/2 - 1));
                return (nbPokemon/2 - 1);
            }
        }
        Log.i("getIndexWeakestPokemon",""+(nbPokemon-1));
        return (nbPokemon - 1);
    }

}