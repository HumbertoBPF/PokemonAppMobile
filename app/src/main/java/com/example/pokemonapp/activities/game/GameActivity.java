package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.models.Trainer.Position.BACK;
import static com.example.pokemonapp.models.Trainer.Position.DEFEATED;
import static com.example.pokemonapp.models.Trainer.Position.FRONT;
import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.goToNextActivityWithStringExtra;
import static com.example.pokemonapp.util.Tools.loadTeam;
import static com.example.pokemonapp.util.Tools.yesOrNoDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.MovesAdapter;
import com.example.pokemonapp.adapters.PokemonAdapter;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.models.Trainer;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private final String TAG = "GameActivity";

    private TextView gameDescription;
    private RecyclerView playerChoicesRecyclerView; // recyclerView containing items to be chosen by the player (moves and pokémon)
    private AlertDialog endGameDialog;
    private AlertDialog quitGameDialog;

    private final Handler handler = new Handler();

    private String gameMode;

    private PokemonDAO pokemonDAO;
    private MoveDAO moveDAO;
    private MoveTypeDAO moveTypeDAO;
    private PokemonTypeDAO pokemonTypeDAO;
    private TypeEffectiveDAO typeEffectiveDAO;
    private TypeNotEffectiveDAO typeNotEffectiveDAO;
    private TypeNoEffectDAO typeNoEffectDAO;

    private List<Pokemon> allPokemon;
    private List<Integer> pokemonChosenByCPU;   // indexes of the pokémon to be chosen by CPU everytime it is necessary

    private final Trainer player = new Trainer();
    private final Trainer cpu = new Trainer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Game activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);

        getLayoutElements();
        getDAOs();

        SharedPreferences sh = getSharedPreferences(getString(R.string.name_shared_preferences_file), MODE_PRIVATE);
        gameMode = sh.getString(getString(R.string.key_game_mode),null);

        quitGameDialog = yesOrNoDialog(this, "Quit game ?", "Do you want to quit the game ?", "Yes", "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quitGameDialog.dismiss();
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        quitGameDialog.dismiss();
                    }
                });

        player.setTeam(loadTeam(this,getString(R.string.filename_json_player_team)));
        cpu.setTeam(loadTeam(this,getString(R.string.filename_json_cpu_team)));
        pokemonChosenByCPU = getDistinctRandomIntegers(0,player.getTeam().size()-1,cpu.getTeam().size());

        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                List<Object> objects = new ArrayList<>();
                allPokemon = pokemonDAO.getPokemonFromLocal();
                objects.addAll(allPokemon);
                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                pickPokemonForCPU();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pickPokemonForPlayer(new OnChoiceListener() {
                            @Override
                            public void onChoice() {
                                battle();
                            }
                        });
                    }
                },6000);
            }
        }).execute();
    }

    private void getLayoutElements() {
        gameDescription = findViewById(R.id.game_description);
        playerChoicesRecyclerView = findViewById(R.id.player_choices_recycler_view);

        player.setCurrentPokemonName(findViewById(R.id.player_pokemon_name));
        player.setCurrentPokemonProgressBarHP(findViewById(R.id.progress_bar_hp_player));
        player.addPokeball(findViewById(R.id.player_pokeball_1));
        player.addPokeball(findViewById(R.id.player_pokeball_2));
        player.addPokeball(findViewById(R.id.player_pokeball_3));
        player.addPokeball(findViewById(R.id.player_pokeball_4));
        player.addPokeball(findViewById(R.id.player_pokeball_5));
        player.addPokeball(findViewById(R.id.player_pokeball_6));
        player.setPokemonImageView(findViewById(R.id.player_pokemon_image));

        cpu.setCurrentPokemonName(findViewById(R.id.cpu_pokemon_name));
        cpu.setCurrentPokemonProgressBarHP(findViewById(R.id.progress_bar_hp_cpu));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_1));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_2));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_3));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_4));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_5));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_6));
        cpu.setPokemonImageView(findViewById(R.id.cpu_pokemon_image));
    }

    private void getDAOs() {
        pokemonDAO = PokemonAppDatabase.getInstance(this).getPokemonDAO();
        moveDAO = PokemonAppDatabase.getInstance(this).getMoveDAO();
        moveTypeDAO = PokemonAppDatabase.getInstance(this).getMoveTypeDAO();
        pokemonTypeDAO = PokemonAppDatabase.getInstance(this).getPokemonTypeDAO();
        typeEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeEffectiveDAO();
        typeNotEffectiveDAO = PokemonAppDatabase.getInstance(this).getTypeNotEffectiveDAO();
        typeNoEffectDAO = PokemonAppDatabase.getInstance(this).getTypeNoEffectDAO();
    }

    private void battle() {
        Log.i(TAG,"Pokemon player HP : "+player.getCurrentPokemon().getPokemonServer().getFHp());
        Log.i(TAG,"CPU player HP : "+cpu.getCurrentPokemon().getPokemonServer().getFHp());
        player.setFlinched(false);
        cpu.setFlinched(false);
        if (player.isPokemonAlive() && cpu.isPokemonAlive()){
            pickMoveForPlayer(new OnChoiceListener() {
                @Override
                public void onChoice() {
                    pickMoveForCpu(new OnChoiceListener() {
                        @Override
                        public void onChoice() {
                            Log.i(TAG,"Pokemon player speed : "+player.getCurrentPokemon().getPokemonServer().getFSpeed());
                            Log.i(TAG,"Pokemon CPU speed : "+cpu.getCurrentPokemon().getPokemonServer().getFSpeed());
                            // according to the speed attribute of the pokémon, we decide which pokémon attacks first
                            if (player.getCurrentPokemon().getPokemonServer().getFSpeed()<cpu.getCurrentPokemon().getPokemonServer().getFSpeed()){
                                attack(cpu.getCurrentPokemon(),player.getCurrentPokemon(),cpu.getCurrentMove(), new OnChoiceListener() {
                                    @Override
                                    public void onChoice() {
                                        if (player.isPokemonAlive() && cpu.isPokemonAlive()){
                                            attack(player.getCurrentPokemon(),cpu.getCurrentPokemon(),player.getCurrentMove(),
                                                    new OnChoiceListener() {
                                                        @Override
                                                        public void onChoice() {
                                                            battle();
                                                        }
                                                    });
                                        }else if (!player.isPokemonAlive()){
                                            onPlayerPokemonDefeat();
                                        }else if (!cpu.isPokemonAlive()){
                                            onCpuPokemonDefeat();
                                        }
                                    }
                                });
                            }else{
                                attack(player.getCurrentPokemon(),cpu.getCurrentPokemon(),player.getCurrentMove(), new OnChoiceListener() {
                                    @Override
                                    public void onChoice() {
                                        if (player.isPokemonAlive() && cpu.isPokemonAlive()){
                                            attack(cpu.getCurrentPokemon(),player.getCurrentPokemon(),cpu.getCurrentMove(),new OnChoiceListener() {
                                                @Override
                                                public void onChoice() {
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            battle();
                                                        }
                                                    },3000);
                                                }
                                            });
                                        }else if (!player.isPokemonAlive()){
                                            onPlayerPokemonDefeat();
                                        }else if (!cpu.isPokemonAlive()){
                                            onCpuPokemonDefeat();
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }else if (!player.isPokemonAlive()){
            onPlayerPokemonDefeat();
        }else if (!cpu.isPokemonAlive()){
            onCpuPokemonDefeat();
        }
    }

    /**
     * Updates the UI when a CPU's pokémon is defeated and decides what to do next.
     */
    public void onCpuPokemonDefeat() {
        // updates UI
        cpu.updateRemainingPokemon();
        gameDescription.setText(getString(R.string.foe_possessive) +
                cpu.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.fainted));
        cpu.setPokemonImageResource(this,DEFEATED);
        // decides the next step
        handler.postDelayed(this::pickAnotherCpuPokemonOrEndgame, 3000);
    }

    /**
     * Updates the UI when a player's pokémon is defeated and decides what to do next.
     */
    private void onPlayerPokemonDefeat() {
        player.updateRemainingPokemon();
        gameDescription.setText(getString(R.string.player_possessive) +
                player.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.fainted));
        player.setPokemonImageResource(this,DEFEATED);
        handler.postDelayed(GameActivity.this::pickAnotherPlayerPokemonOrEndGame, 3000);
    }

    /**
     * Decides what to do after the defeat of a CPU's pokémon (finish the game or select another
     * pokémon).
     */
    private void pickAnotherCpuPokemonOrEndgame() {
        if (!pokemonChosenByCPU.isEmpty()){ // if the CPU still has pokémon to use, pick one and battle again
            Log.i(TAG,"CPU changes its pokemon");

            // we reset some attributes of the pokémon that was defeated
            cpu.reset();
            // the nbOfTurnsTrapped of the pokémon that stays on the field is set to 0 since the pokémon
            // which was trapping it was defeated
            player.setNbOfTurnsTrapped(0);

            pickPokemonForCPU();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    battle();
                }
            },6000);
        }else{  // otherwise, finish game
            Log.i(TAG,"CPU was defeated");
            endGame(R.string.player_win_msg);
        }
    }

    /**
     * Decides what to do after the defeat of a player's pokémon (finish the game or select another
     * pokémon).
     */
    private void pickAnotherPlayerPokemonOrEndGame() {
        if (getNbOfRemainingPokemon(player) > 0){   // if the player still has pokémon to use, pick one and battle again
            Log.i(TAG,"Player changes its pokemon");

            // we reset some attributes of the pokémon that was defeated
            player.reset();
            // the nbOfTurnsTrapped of the pokémon that stays on the field is set to 0 since the pokémon
            // which was trapping it was defeated
            cpu.setNbOfTurnsTrapped(0);

            pickPokemonForPlayer(new OnChoiceListener() {
                @Override
                public void onChoice() {
                    battle();
                }
            });
        }else{  // otherwise, finish game
            Log.i(TAG,"Player was defeated");
            endGame(R.string.cpu_win_msg);
        }
    }

    private void endGame(int idResultString) {
        gameDescription.setText(idResultString);
        endGameDialog = yesOrNoDialog(this, getString(idResultString),
                "Would you like to play again this mode ?", "Yes", "No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endGameDialog.dismiss();
                        goToNextActivityWithStringExtra(GameActivity.this,getString(R.string.key_game_mode),
                                gameMode,PokemonSelectionActivity.class);
                        finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endGameDialog.dismiss();
                        finish();
                    }
                });
        endGameDialog.show();
    }

    /**
     * @param trainer concerned trainer
     * @return number of pokémon whose HP is greater than 0 (i.e. that have not fainted yet)
     */
    private int getNbOfRemainingPokemon(Trainer trainer){
        int nbOfRemainingPokemon = 0;
        for (InGamePokemon inGamePokemon : trainer.getTeam()){
            if (inGamePokemon.getPokemonServer().getFHp() > 0){
                nbOfRemainingPokemon++;
            }
        }
        Log.i(TAG,"nbOfRemainingPokemon = "+nbOfRemainingPokemon);
        return nbOfRemainingPokemon;
    }

    /**
     * Asks the player to choose a pokémon by showing and configuring the RecyclerView with the pokémon
     * @param onChoiceListener code to be executed after the player's choice.
     */
    private void pickPokemonForPlayer(OnChoiceListener onChoiceListener){
        // ask the player to choose a pokémon and shows the options
        gameDescription.setText(R.string.choose_pokemon_msg);
        playerChoicesRecyclerView.setVisibility(View.VISIBLE);

        playerChoicesRecyclerView.setAdapter(new PokemonAdapter(getApplicationContext(), getAlivePokemonPlayer(),
            new PokemonAdapter.OnClickListener() {
                @Override
                public void onClick(View view, Pokemon pokemon) {
                    // selects the InGamePokémon corresponding to the selected pokémon (same id)
                    for (InGamePokemon inGamePokemon : player.getTeam()){
                        if (inGamePokemon.getPokemonServer().getFId().equals(pokemon.getFId())){
                            player.setCurrentPokemon(inGamePokemon);
                            playerChoicesRecyclerView.setVisibility(View.GONE);
                        }
                    }
                    // announces player's choice
                    Pokemon pokemonServerPlayer = player.getCurrentPokemon().getPokemonServer();
                    gameDescription.setText(getString(R.string.player_chooses)+pokemonServerPlayer.getFName());
                    // update pokémon data UI after a while
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            player.setPokemonImageResource(getApplicationContext(),BACK);
                            player.setHpBar(getApplicationContext(),allPokemon);
                            player.updateCurrentPokemonName();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onChoiceListener.onChoice();
                                }
                            },3000);
                        }
                    },3000);
                }
            })
        );
    }

    /**
     * Returns the list of player's pokémon that are still alive.
     */
    @NonNull
    private List<Object> getAlivePokemonPlayer() {
        List<Object> pokemonList = new ArrayList<>();
        for (InGamePokemon inGamePokemon : player.getTeam()){
            Pokemon pokemon = inGamePokemon.getPokemonServer();
            if (pokemon.getFHp() > 0){
                pokemonList.add(pokemon);
            }
        }
        return pokemonList;
    }

    /**
     * Picks the next available pokémon for the CPU and updates the pokémon data UI.
     */
    private void pickPokemonForCPU(){
        if (!pokemonChosenByCPU.isEmpty()){
            // chooses the next pokémon available and removes its index from the list 'pokemonChosenByCPU'
            cpu.setCurrentPokemon(cpu.getTeam().get(pokemonChosenByCPU.get(0)));
            pokemonChosenByCPU.remove(0);
            gameDescription.setText(getString(R.string.cpu_chooses)+cpu.getCurrentPokemon().getPokemonServer().getFName());
            // updates pokémon data UI after a while
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    cpu.setPokemonImageResource(getApplicationContext(),FRONT);
                    cpu.setHpBar(getApplicationContext(), allPokemon);
                    cpu.updateCurrentPokemonName();
                }
            },3000);
        }else{
            Toast.makeText(this,"The game is finished",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Manage the selection of a move for the player's pokémon by either asking the player to select a move
     * when there are remaining moves (i.e. moves whose number of pp is greater than 0) to be selected
     * or selecting struggle when there is no remaining move.
     * @param onChoiceListener code to be executed after the player's choice
     */
    private void pickMoveForPlayer(OnChoiceListener onChoiceListener){
        if (!player.isLoading()){
            List<Move> moves = getRemainingMoves(player.getCurrentPokemon());
            if (moves.isEmpty()){   // if there is no move remaining, use struggle
                new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                    @Override
                    public List<Object> doInBackground() {
                        Move move = moveDAO.getMoveByName("Struggle");
                        List<Object> objects = new ArrayList<>();
                        objects.add(move);
                        return objects;
                    }

                    @Override
                    public void onPostExecute(List<Object> objects) {
                        player.setCurrentMove((Move) objects.get(0));
                        onChoiceListener.onChoice();
                    }
                }).execute();
            }else{  // else, ask the player to choose a mode by presenting the moves in a RecyclerView
                gameDescription.setText(R.string.choose_move_msg);
                playerChoicesRecyclerView.setVisibility(View.VISIBLE);
                List<Object> moveObjects = new ArrayList<>();
                moveObjects.addAll(moves);
                playerChoicesRecyclerView.setAdapter(new MovesAdapter(this, moveObjects, new MovesAdapter.OnClickListener() {
                    @Override
                    public void onClick(View view, Move move) {
                        player.setCurrentMove(move);
                        playerChoicesRecyclerView.setVisibility(View.GONE);
                        onChoiceListener.onChoice();
                    }
                }));
            }
        }else{  // if pokémon is loading an attack or reloading, skips this step
            onChoiceListener.onChoice();
        }
    }

    /**
     * @param inGamePokemon pokémon concerned.
     * @return List of moves whose the number of pp is greater than 0 .
     */
    private List<Move> getRemainingMoves(InGamePokemon inGamePokemon) {
        List<Move> moves = new ArrayList<>();
        for (Move move : inGamePokemon.getMoves()){
            if (move.getFPp() > 0){
                moves.add(move);
            }
        }
        return moves;
    }

    /**
     * Manage the choice of a move for the CPU. This choice is random when there are remaining moves
     * (i.e. moves whose number of pps is greater than 0). Otherwise, the move Struggle is picked.
     * @param onChoiceListener code to be executed after the choice of a move for the CPU
     */
    private void pickMoveForCpu(OnChoiceListener onChoiceListener){
        if (!cpu.isLoading()) {
            List<Move> moves = getRemainingMoves(cpu.getCurrentPokemon());
            if (moves.isEmpty()) {  // if there is no move remaining, use struggle
                new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                    @Override
                    public List<Object> doInBackground() {
                        Move move = moveDAO.getMoveByName("Struggle");
                        List<Object> objects = new ArrayList<>();
                        objects.add(move);
                        return objects;
                    }

                    @Override
                    public void onPostExecute(List<Object> objects) {
                        cpu.setCurrentMove((Move) objects.get(0));
                        onChoiceListener.onChoice();
                    }
                }).execute();
            } else {    // else, pick randomly a move among the remaining ones
                int randomIndex = getDistinctRandomIntegers(0, moves.size() - 1, 1).get(0);
                cpu.setCurrentMove(moves.get(randomIndex));
                onChoiceListener.onChoice();
            }
        }else{  // if pokémon is loading an attack or reloading, skips this step
            onChoiceListener.onChoice();
        }
    }

    private void attack(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, Move move, OnChoiceListener onChoiceListener){
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {
                Pokemon attackingPokemonServer = attackingPokemon.getPokemonServer();
                Pokemon defendingPokemonServer = defendingPokemon.getPokemonServer();

                Long moveType = moveTypeDAO.getTypesOfMove(move.getFId()).get(0).getFId();
                List<Long> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(attackingPokemonServer.getFId());
                List<Long> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(defendingPokemonServer.getFId());

                Log.i(TAG,"Move type : "+moveType);
                Log.i(TAG,"Attacking pokémon types : "+attackingPokemonTypes.toString());
                Log.i(TAG,"Defending pokémon types : "+defendingPokemonTypes.toString());

                List<Long> effectiveTypes = typeEffectiveDAO.getEffectiveTypesIds(moveType);
                List<Long> notEffectiveTypes = typeNotEffectiveDAO.getNotEffectiveTypesIds(moveType);
                List<Long> noEffectType = typeNoEffectDAO.getNoEffectTypesIds(moveType);

                Log.i(TAG,"Effective types : "+effectiveTypes.toString());
                Log.i(TAG,"Not effective types : "+notEffectiveTypes.toString());
                Log.i(TAG,"No effect types : "+noEffectType.toString());

                List<Object> objects = new ArrayList<>();
                objects.add(moveType);
                objects.add(attackingPokemonTypes);
                objects.add(defendingPokemonTypes);
                objects.add(effectiveTypes);
                objects.add(notEffectiveTypes);
                objects.add(noEffectType);

                return objects;
            }

            @Override
            public void onPostExecute(List<Object> objects) {
                Pokemon attackingPokemonServer = attackingPokemon.getPokemonServer();

                Long moveType = (Long) objects.get(0);
                List<Long> attackingPokemonTypes = (List<Long>) objects.get(1);
                List<Long> defendingPokemonTypes = (List<Long>) objects.get(2);
                List<Long> effectiveTypes = (List<Long>) objects.get(3);
                List<Long> notEffectiveTypes = (List<Long>) objects.get(4);
                List<Long> noEffectType = (List<Long>) objects.get(5);

                double stab = attackingPokemonTypes.contains(moveType) ? 1.5 : 1.0;
                double typeFactor = computeTypeFactor(defendingPokemonTypes, effectiveTypes, notEffectiveTypes, noEffectType);
                String messageEffectiveness = getMessageEffectiveness(typeFactor);

                int minHits = move.getFMinTimesPerTour();
                int maxHits = move.getFMaxTimesPerTour();

                int nbOfHits = getDistinctRandomIntegers(minHits,maxHits,1).get(0);

                if (move.getFRoundsToLoad() == 1){
                    if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                        if (!player.isLoading()){
                            player.setLoading(true);
                            gameDescription.setText(getString(R.string.player_possessive)+attackingPokemonServer.getFName()+getString(R.string.loads_attack));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finishAttackTurn(defendingPokemon,onChoiceListener);
                                }
                            },3000);
                            return;
                        }
                    }else{
                        if (!cpu.isLoading()){
                            cpu.setLoading(true);
                            gameDescription.setText(getString(R.string.foe_possessive)+attackingPokemonServer.getFName()+getString(R.string.loads_attack));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finishAttackTurn(defendingPokemon,onChoiceListener);
                                }
                            },3000);
                            return;
                        }
                    }
                }

                if (move.getFRoundsToLoad() == -1){
                    if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                        if (player.isLoading()){
                            player.setLoading(false);
                            gameDescription.setText(getString(R.string.player_possessive)+attackingPokemonServer.getFName()+getString(R.string.is_reloading));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finishAttackTurn(defendingPokemon,onChoiceListener);
                                }
                            },3000);
                            return;
                        }
                    }else{
                        if (cpu.isLoading()){
                            cpu.setLoading(false);
                            gameDescription.setText(getString(R.string.foe_possessive)+attackingPokemonServer.getFName()+getString(R.string.is_reloading));
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finishAttackTurn(defendingPokemon,onChoiceListener);
                                }
                            },3000);
                            return;
                        }
                    }
                }

                if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())) {
                    if (player.isFlinched()){
                        gameDescription.setText(getString(R.string.player_possessive)+attackingPokemonServer.getFName()+" is flinched.");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finishAttackTurn(defendingPokemon,onChoiceListener);
                            }
                        },3000);
                        return;
                    }
                }else{
                    if (cpu.isFlinched()){
                        gameDescription.setText(getString(R.string.player_possessive)+attackingPokemonServer.getFName()+" is flinched.");
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finishAttackTurn(defendingPokemon,onChoiceListener);
                            }
                        },3000);
                        return;
                    }
                }

                if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())) {
                    gameDescription.setText(getString(R.string.player_possessive) + player.getCurrentPokemon().getPokemonServer().getFName() +
                            getString(R.string.used) + player.getCurrentMove().getFName());
                }else{
                    gameDescription.setText(getString(R.string.foe_possessive) + cpu.getCurrentPokemon().getPokemonServer().getFName() +
                            getString(R.string.used) + cpu.getCurrentMove().getFName());
                }

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hit(defendingPokemon, move, stab, typeFactor, messageEffectiveness,1, nbOfHits, onChoiceListener);
                    }
                }, 3000);
            }
        }).execute();
    }

    private void hit(InGamePokemon defendingPokemon, Move move, double stab, double typeFactor,
                     String messageEffectiveness, int currentHit, int nbOfHits, OnChoiceListener onChoiceListener) {

        if (defendingPokemon.getPokemonServer().getFHp() <= 0){
            onChoiceListener.onChoice();
        }else{
            double damage;
            if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                damage = player.hitOpponent(defendingPokemon,currentHit,move,stab,typeFactor);
                if (damage == -1){
                    gameDescription.setText(R.string.attack_missed_msg);
                }else{
                    gameDescription.setText(messageEffectiveness);
                    cpu.receiveDamage(damage,move);
                }
            }else{
                damage = cpu.hitOpponent(defendingPokemon,currentHit,move,stab,typeFactor);
                if (damage == -1){
                    gameDescription.setText(R.string.attack_missed_msg);
                }else{
                    gameDescription.setText(messageEffectiveness);
                    player.receiveDamage(damage,move);
                }
            }

            player.setHpBar(this, allPokemon);
            cpu.setHpBar(this, allPokemon);

            hitAgainOrStop(defendingPokemon, move, stab, typeFactor, messageEffectiveness, currentHit, nbOfHits, onChoiceListener);
        }
    }

    private void hitAgainOrStop(InGamePokemon defendingPokemon, Move move, double stab, double typeFactor, String messageEffectiveness, int currentHit, int nbOfHits, OnChoiceListener onChoiceListener) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nbOfHits > 1){
                    gameDescription.setText(getString(R.string.hit)+ currentHit +getString(R.string.times));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentHit < nbOfHits){
                                hit(defendingPokemon, move, stab, typeFactor, messageEffectiveness, currentHit +1, nbOfHits, onChoiceListener);
                            }else if (currentHit == nbOfHits){
                                finishAttackTurn(defendingPokemon, onChoiceListener);
                            }
                        }
                    },3000);
                }else{
                    finishAttackTurn(defendingPokemon, onChoiceListener);
                }
            }
        },3000);
    }

    private void finishAttackTurn(InGamePokemon defendingPokemon, OnChoiceListener onChoiceListener) {
        int delay = 0;
        if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())) {
            if (cpu.receiveWrappingDamage(allPokemon)) {
                gameDescription.setText(getString(R.string.foe_possessive) + cpu.getCurrentPokemon().getPokemonServer().getFName() +
                        getString(R.string.trapping_damage));
                delay = 3000;
            }
        } else {
            if (player.receiveWrappingDamage(allPokemon)) {
                gameDescription.setText(getString(R.string.player_possessive) + player.getCurrentPokemon().getPokemonServer().getFName() +
                        getString(R.string.trapping_damage));
                delay = 3000;
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                player.setHpBar(getApplicationContext(), allPokemon);
                cpu.setHpBar(getApplicationContext(), allPokemon);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onChoiceListener.onChoice();
                    }
                },3000);
            }
        }, delay);
    }

    private String getMessageEffectiveness(double typeFactor) {
        String messageEffectiveness = "";
        if (typeFactor > 1){
            messageEffectiveness = getString(R.string.super_effective_msg);
        }else if (typeFactor < 1 && typeFactor >0){
            messageEffectiveness = getString(R.string.not_effective_msg);
        }else if (typeFactor == 0){
            messageEffectiveness = getString(R.string.no_effect_msg);
        }
        return messageEffectiveness;
    }

    private double computeTypeFactor(List<Long> defendingPokemonTypes, List<Long> effectiveTypes, List<Long> notEffectiveTypes, List<Long> noEffectType) {
        double typeFactor = 1.0;
        for (Long typeDefending : defendingPokemonTypes){
            if (effectiveTypes.contains(typeDefending)){
                typeFactor *= 2;
            }
            if (notEffectiveTypes.contains(typeDefending)){
                typeFactor *= 0.5;
            }
            if (noEffectType.contains(typeDefending)){
                typeFactor *= 0;
            }
        }
        return typeFactor;
    }

    @Override
    public void onBackPressed() {
        if (!quitGameDialog.isShowing()){
            quitGameDialog.show();
        }
    }

    interface OnChoiceListener {
        void onChoice();
    }

}