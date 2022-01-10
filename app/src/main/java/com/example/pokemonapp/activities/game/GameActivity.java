package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.models.Trainer.Position.BACK;
import static com.example.pokemonapp.models.Trainer.Position.DEFEATED;
import static com.example.pokemonapp.models.Trainer.Position.FRONT;
import static com.example.pokemonapp.util.Tools.dualButtonDialog;
import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.Tools.goToNextActivityWithStringExtra;
import static com.example.pokemonapp.util.Tools.loadTeam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
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
import com.example.pokemonapp.entities.Type;
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
    private String gameLevel;

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

    private MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);

        getLayoutElements();
        getDAOs();

        SharedPreferences sh = getSharedPreferences(getString(R.string.name_shared_preferences_file), MODE_PRIVATE);
        gameMode = sh.getString(getString(R.string.key_game_mode),null);
        gameLevel = sh.getString(getString(R.string.key_game_level),getString(R.string.easy_level));    // getting difficult level

        mp = MediaPlayer.create(this, R.raw.battle);
        mp.setLooping(true);
        mp.start();

        quitGameDialog = dualButtonDialog(this, getString(R.string.quit_game_dialog_title), getString(R.string.quit_game_dialog_text), getString(R.string.yes), getString(R.string.no),
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
        quitGameDialog.setCancelable(false);

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
        player.setCurrentPokemonImageView(findViewById(R.id.player_pokemon_image));

        cpu.setCurrentPokemonName(findViewById(R.id.cpu_pokemon_name));
        cpu.setCurrentPokemonProgressBarHP(findViewById(R.id.progress_bar_hp_cpu));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_1));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_2));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_3));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_4));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_5));
        cpu.addPokeball(findViewById(R.id.cpu_pokeball_6));
        cpu.setCurrentPokemonImageView(findViewById(R.id.cpu_pokemon_image));
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
    private void onCpuPokemonDefeat() {
        // updates UI
        cpu.updateRemainingPokemon();
        gameDescription.setText(getString(R.string.foe_possessive) +
                cpu.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.fainted));
        cpu.setPokemonImageResource(this,DEFEATED);
        // plays fainting sound
        playsFaintSound();
        // decides the next step
        handler.postDelayed(this::pickAnotherCpuPokemonOrEndgame, 3000);
    }

    /**
     * Updates the UI when a player's pokémon is defeated and decides what to do next.
     */
    private void onPlayerPokemonDefeat() {
        // updates UI
        player.updateRemainingPokemon();
        gameDescription.setText(getString(R.string.player_possessive) +
                player.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.fainted));
        player.setPokemonImageResource(this,DEFEATED);
        // plays fainting sound
        playsFaintSound();
        // decides the next step
        handler.postDelayed(GameActivity.this::pickAnotherPlayerPokemonOrEndGame, 3000);
    }

    private void playsFaintSound() {
        MediaPlayer mpLocal = MediaPlayer.create(this, R.raw.faint);
        mpLocal.start();
        mpLocal.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mpLocal.release();
            }
        });
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
            player.setNbTurnsTrapped(0);

            pickPokemonForCPU();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    battle();
                }
            },6000);
        }else{  // otherwise, finish game
            Log.i(TAG,"CPU was defeated");
            endGame(true);
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
            cpu.setNbTurnsTrapped(0);

            pickPokemonForPlayer(new OnChoiceListener() {
                @Override
                public void onChoice() {
                    battle();
                }
            });
        }else{  // otherwise, finish game
            Log.i(TAG,"Player was defeated");
            endGame(false);
        }
    }

    /**
     * Shows the result to the player in the TextView on the bottom of the screen and shows a play
     * again dialog.
     * @param playerWon boolean indicating if player won the battle
     */
    private void endGame(boolean playerWon) {
        int idResultString;
        int idResultAudio;
        if (playerWon){
            idResultString = R.string.player_win_msg;
            idResultAudio = R.raw.success;
        }else{
            idResultString = R.string.cpu_win_msg;
            idResultAudio = R.raw.fail;
        }
        gameDescription.setText(idResultString);
        endGameDialog = dualButtonDialog(this, getString(idResultString),
                getString(R.string.play_again_dialog_text), getString(R.string.yes), getString(R.string.no),
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
        endGameDialog.setCancelable(false);
        mp.stop();
        mp = MediaPlayer.create(this, idResultAudio);
        mp.start();
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
     * Returns the list of player's pokémon that are still alive (whose HP is greater than 0).
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
     * or selecting 'Struggle' when there is no remaining move.
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
     * @return list of moves whose the number of pp is greater than 0.
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
     * (i.e. moves whose number of pps is greater than 0). Otherwise, the move 'Struggle' is picked.
     * @param onChoiceListener code to be executed after the choice of a move for the CPU.
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

                new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                    @Override
                    public List<Object> doInBackground() {

                        int indexChosenMove = 0;

                        if (!gameLevel.equals(getString(R.string.easy_level))){ // artificial intelligence for game levels different from easy
                            // gets the types of the attacking pokémon
                            List<Type> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemon(cpu.getCurrentPokemon().getPokemonServer().getFId());
                            Type attackingPokemonType1 = attackingPokemonTypes.get(0);
                            Type attackingPokemonType2 = null;
                            if (attackingPokemonTypes.size() == 2){ // get type 2 only if it exists
                                attackingPokemonType2 = attackingPokemonTypes.get(1);
                            }

                            // gets the types of the defending pokémon
                            List<Type> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemon(player.getCurrentPokemon().getPokemonServer().getFId());
                            Type defendingPokemonType1 = defendingPokemonTypes.get(0);
                            Type defendingPokemonType2 = null;
                            if (defendingPokemonTypes.size() == 2){ // get type 2 only if it exists
                                defendingPokemonType2 = defendingPokemonTypes.get(1);
                            }

                            List<Double> qualityFactors = new ArrayList<>();    // number used to define what is the best move
                            for (Move move : moves){
                                Log.i(TAG,"===============================Analysing move : "+move.getFName()+"===============================");

                                Type moveType = moveTypeDAO.getTypesOfMove(move.getFId()).get(0);   // get type of the current move

                                // get the types against which this move is effective, not effective and ineffective
                                List<Long> effectiveTypesIds = typeEffectiveDAO.getEffectiveTypesIds(moveType.getFId());
                                List<Long> notEffectiveTypesIds = typeNotEffectiveDAO.getNotEffectiveTypesIds(moveType.getFId());
                                List<Long> noEffectTypesIds = typeNoEffectDAO.getNoEffectTypesIds(moveType.getFId());

                                // verifies stab
                                double qualityFactor = 1;
                                if (attackingPokemonType1.getFId().equals(moveType.getFId())){
                                    qualityFactor *= 1.5;
                                }
                                if (attackingPokemonType2 != null){
                                    if (attackingPokemonType2.getFId().equals(moveType.getFId())){
                                        qualityFactor *= 1.5;
                                    }
                                }

                                // verifies if the primary type of the defending pokémon is weak or resistant against this move
                                if (effectiveTypesIds.contains(defendingPokemonType1.getFId())){
                                    qualityFactor *= 2;
                                }
                                if (notEffectiveTypesIds.contains(defendingPokemonType1.getFId())){
                                    qualityFactor *= 0.5;
                                }
                                if (noEffectTypesIds.contains(defendingPokemonType1.getFId())){
                                    qualityFactor *= 0;
                                }

                                // verifies if the secondary type of the defending pokémon (if it exists)
                                // is weak or resistant against this move
                                if (defendingPokemonType2 != null){
                                    if (effectiveTypesIds.contains(defendingPokemonType2.getFId())){
                                        qualityFactor *= 2;
                                    }
                                    if (notEffectiveTypesIds.contains(defendingPokemonType2.getFId())){
                                        qualityFactor *= 0.5;
                                    }
                                    if (noEffectTypesIds.contains(defendingPokemonType2.getFId())){
                                        qualityFactor *= 0;
                                    }
                                }

                                // avoids to use moves for which the user faints when the HP is not low enough
                                if (move.getFUserFaints() && cpu.getCurrentPokemon().getPokemonServer().getFHp() > 100){
                                    qualityFactor*=0;
                                }

                                // reflects the risk of choosing a move with low accuracy (may miss the attack)
                                if (move.getFAccuracy() < 85){
                                    qualityFactor*=0.75;
                                }

                                // reflects the fact that to load such moves, we do not attack during one turn
                                if (move.getFRoundsToLoad() != 0){
                                    qualityFactor*=0.5;
                                }

                                // reflects the fact that the Hp is incremented with half of the damage for some moves
                                if (move.getFRecoversHp()){
                                    qualityFactor*=1.5;
                                }

                                // takes into account the power of the move
                                qualityFactor *= move.getFPower();
                                // takes into account the minimum number of times that a move can hit per turn
                                qualityFactor *= move.getFMinTimesPerTour();

                                Log.i(TAG,"===============================Quality factor = "+qualityFactor+"===============================");

                                qualityFactors.add(qualityFactor);
                            }

                            // picks the move whose quality factor is the greatest one
                            double greatestQualityFactor = qualityFactors.get(0);
                            for (int i = 0;i<qualityFactors.size();i++){
                                double currentQualityFactor = qualityFactors.get(i);
                                if (currentQualityFactor > greatestQualityFactor){
                                    indexChosenMove = i;
                                    greatestQualityFactor = currentQualityFactor;
                                }
                            }
                        }else{  // for the easy level, the choice of the moves is random
                            indexChosenMove = getDistinctRandomIntegers(0, moves.size() - 1, 1).get(0);
                        }

                        List<Object> objects = new ArrayList<>();
                        objects.add(moves.get(indexChosenMove));
                        return objects;
                    }

                    @Override
                    public void onPostExecute(List<Object> objects) {
                        cpu.setCurrentMove((Move) objects.get(0));
                        onChoiceListener.onChoice();
                    }
                }).execute();
            }
        }else{  // if pokémon is loading an attack or reloading, skips this step
            onChoiceListener.onChoice();
        }
    }

    private void attack(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, Move move, OnChoiceListener onChoiceListener){
        new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
            @Override
            public List<Object> doInBackground() {

                // get the attacking and defending pokémon as well as their types (the types from local DB)
                Pokemon attackingPokemonServer = attackingPokemon.getPokemonServer();
                Pokemon defendingPokemonServer = defendingPokemon.getPokemonServer();

                Long moveType = moveTypeDAO.getTypesOfMove(move.getFId()).get(0).getFId();
                List<Long> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(attackingPokemonServer.getFId());
                List<Long> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(defendingPokemonServer.getFId());

                Log.i(TAG,"Move type : "+moveType);
                Log.i(TAG,"Attacking pokémon types : "+attackingPokemonTypes.toString());
                Log.i(TAG,"Defending pokémon types : "+defendingPokemonTypes.toString());

                // get the types against which the type of the attack is effective, not effective and not effective at all
                // from local DB
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
                Long moveType = (Long) objects.get(0);
                List<Long> attackingPokemonTypes = (List<Long>) objects.get(1);
                List<Long> defendingPokemonTypes = (List<Long>) objects.get(2);
                List<Long> effectiveTypes = (List<Long>) objects.get(3);
                List<Long> notEffectiveTypes = (List<Long>) objects.get(4);
                List<Long> noEffectType = (List<Long>) objects.get(5);

                // if the attacking pokémon has the same type of the move that is inflicted, the move
                // get a damage bonus of 50%
                double stab = attackingPokemonTypes.contains(moveType) ? 1.5 : 1.0;
                // factor due to the effectiveness of the type of the move against the ones of the defending pokémon
                double typeFactor = computeTypeFactor(defendingPokemonTypes, effectiveTypes, notEffectiveTypes, noEffectType);
                // message to be shown as a feedback about the typeFactor
                String messageEffectiveness = getMessageEffectiveness(typeFactor);

                int minHits = move.getFMinTimesPerTour();
                int maxHits = move.getFMaxTimesPerTour();

                int nbOfHits = getDistinctRandomIntegers(minHits,maxHits,1).get(0);

                // skip the turn, if the move needs to be loaded or if the attacking pokémon is reloading or flinched
                if (isLoading(attackingPokemon, defendingPokemon, move, onChoiceListener) ||
                        isReloading(attackingPokemon, defendingPokemon, move, onChoiceListener) ||
                        isFlinched(attackingPokemon, defendingPokemon, onChoiceListener)){
                    return;
                }

                // shows to the player the move that is going to be used
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

    /**
     * @param attackingPokemon pokémon that is using the move
     * @param defendingPokemon pokémon that receives the attack
     * @param move move being used by the attacking pokémon
     * @param onChoiceListener code to be executed at the end of the turn
     * @return a boolean indicating if this turn should be skipped to loading the move
     */
    private boolean isLoading(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, Move move, OnChoiceListener onChoiceListener) {
        if (move.getFRoundsToLoad() == 1){
            if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                if (!player.isLoading()){ // no need to set to load if pokémon is already loading
                    playerLoadsAttack(attackingPokemon, defendingPokemon, onChoiceListener);
                    return true;
                }
            }else{
                if (!cpu.isLoading()){ // no need to set to load if pokémon is already loading
                    cpuLoadsAttack(attackingPokemon, defendingPokemon, onChoiceListener);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param attackingPokemon pokémon that is using the move
     * @param defendingPokemon pokémon that receives the attack
     * @param move move being used by the attacking pokémon
     * @param onChoiceListener code to be executed at the end of the turn
     * @return a boolean indicating if this turn should be skipped to reload the move
     */
    private boolean isReloading(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, Move move, OnChoiceListener onChoiceListener) {
        if (move.getFRoundsToLoad() == -1){
            if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                if (player.isLoading()){
                    playerReloadsAttack(attackingPokemon, defendingPokemon, onChoiceListener);
                    return true;
                }
            }else{
                if (cpu.isLoading()){
                    cpuReloadsAttack(attackingPokemon, defendingPokemon, onChoiceListener);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param attackingPokemon pokémon that is using the move
     * @param defendingPokemon pokémon that receives the attack
     * @param onChoiceListener code to be executed at the end of the turn
     * @return a boolean indicating if this turn should be skipped because the attacking pokémon is flinched
     */
    private boolean isFlinched(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, OnChoiceListener onChoiceListener) {
        if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())) {
            if (player.isFlinched()){
                gameDescription.setText(getString(R.string.player_possessive) + attackingPokemon.getPokemonServer().getFName() +
                        getString(R.string.is_flinched));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishAttackTurn(defendingPokemon, onChoiceListener);
                    }
                },3000);
                return true;
            }
        }else{
            if (cpu.isFlinched()){
                gameDescription.setText(getString(R.string.player_possessive) + attackingPokemon.getPokemonServer().getFName() +
                        getString(R.string.is_flinched));
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finishAttackTurn(defendingPokemon, onChoiceListener);
                    }
                },3000);
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the player's pokémon to load the move and makes the necessary UI changes.
     * @param attackingPokemon pokémon that will use the move being loaded.
     * @param defendingPokemon pokémon that will receive the move.
     * @param onChoiceListener code to be executed at the end of this round.
     */
    private void playerLoadsAttack(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, OnChoiceListener onChoiceListener) {
        player.setLoading(true);    // set to false, because in the next turn the trainer will not be able to pick a move
        gameDescription.setText(getString(R.string.player_possessive) + attackingPokemon.getPokemonServer().getFName() +
                getString(R.string.loads_attack));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAttackTurn(defendingPokemon,onChoiceListener);
            }
        },3000);
    }

    /**
     * Makes the player to finish realoding and makes the necessary UI changes.
     * @param attackingPokemon pokémon that used the move being reloaded.
     * @param defendingPokemon pokémon that received the move.
     * @param onChoiceListener code to be executed at the end of this round.
     */
    private void playerReloadsAttack(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, OnChoiceListener onChoiceListener) {
        player.setLoading(false);   // set to false, because in the next turn the trainer will be able to pick a move
        gameDescription.setText(getString(R.string.player_possessive) + attackingPokemon.getPokemonServer().getFName() +
                getString(R.string.is_reloading));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAttackTurn(defendingPokemon,onChoiceListener);
            }
        },3000);
    }

    /**
     * Sets the cpu's pokémon to load the move and makes the necessary UI changes.
     * @param attackingPokemon pokémon that will use the move being loaded.
     * @param defendingPokemon pokémon that will receive the move.
     * @param onChoiceListener code to be executed at the end of this round.
     */
    private void cpuLoadsAttack(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, OnChoiceListener onChoiceListener) {
        cpu.setLoading(true);   // set to false, because in the next turn the trainer will not be able to pick a move
        gameDescription.setText(getString(R.string.foe_possessive) + attackingPokemon.getPokemonServer().getFName() +
                getString(R.string.loads_attack));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAttackTurn(defendingPokemon,onChoiceListener);
            }
        },3000);
    }

    /**
     * Makes the cpu to finish realoding and makes the necessary UI changes.
     * @param attackingPokemon pokémon that used the move being reloaded.
     * @param defendingPokemon pokémon that received the move.
     * @param onChoiceListener code to be executed at the end of this round.
     */
    private void cpuReloadsAttack(InGamePokemon attackingPokemon, InGamePokemon defendingPokemon, OnChoiceListener onChoiceListener) {
        cpu.setLoading(false);  // set to false, because in the next turn the trainer will be able to pick a move
        gameDescription.setText(getString(R.string.foe_possessive) + attackingPokemon.getPokemonServer().getFName() +
                getString(R.string.is_reloading));
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finishAttackTurn(defendingPokemon,onChoiceListener);
            }
        },3000);
    }

    private void hit(InGamePokemon defendingPokemon, Move move, double stab, double typeFactor,
                     String messageEffectiveness, int currentHit, int nbOfHits, OnChoiceListener onChoiceListener) {

        if (defendingPokemon.getPokemonServer().getFHp() <= 0){
            onChoiceListener.onChoice();
        }else{
            double damage;
            if (defendingPokemon.getId().equals(cpu.getCurrentPokemon().getId())){
                damage = player.hitOpponent(currentHit,move,stab,typeFactor,allPokemon); // gets the damage
                if (damage == -1){ // defending pokémon processes the move received and the UI is updated according to the result
                    gameDescription.setText(R.string.attack_missed_msg);
                }else{
                    gameDescription.setText(messageEffectiveness);
                    cpu.receiveDamage(damage,move);
                }
            }else{
                damage = cpu.hitOpponent(currentHit,move,stab,typeFactor,allPokemon);
                if (damage == -1){
                    gameDescription.setText(R.string.attack_missed_msg);
                }else{
                    gameDescription.setText(messageEffectiveness);
                    player.receiveDamage(damage,move);
                }
            }

            // updates HP bar (for the case when the pokémon faints after using a move, maybe it
            // can be done in a better way)
            player.setHpBar(this, allPokemon);
            cpu.setHpBar(this, allPokemon);

            hitAgainOrStop(defendingPokemon, move, stab, typeFactor, messageEffectiveness, currentHit, nbOfHits, onChoiceListener);
        }
    }

    /**
     * Decides if the move must hit the opponent again or if the turn is finished.
     * @param defendingPokemon opponent pokémon (i.e. pokémon that receives the attack).
     * @param move move that is being used.
     * @param stab stab factor (bonus of 50% over damage when the type of the attack matches the type of the user).
     * @param typeFactor type factor derived from the relationship between the type of the move and the types of defendingPokémon.
     * @param messageEffectiveness message reflecting the effectiveness of the move.
     * @param currentHit number of times that this move has already touched the opponent this turn + 1.
     * @param nbOfHits number of hits that this move is intended to touch the opponent this turn.
     * @param onChoiceListener code to be executed at the end of the current turn.
     */
    private void hitAgainOrStop(InGamePokemon defendingPokemon, Move move, double stab, double typeFactor, String messageEffectiveness, int currentHit, int nbOfHits, OnChoiceListener onChoiceListener) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nbOfHits > 1){ // if this move must hit more than once, we keep hitting until reaching the number of hits
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
                }else{ // else we can finish the turn
                    finishAttackTurn(defendingPokemon, onChoiceListener);
                }
            }
        },3000);
    }

    /**
     * Finishes the turn by inflicting wrapping damage on the opponent and executing the code specified
     * in the listener.
     * @param defendingPokemon pokémon that receives the attack this turn.
     * @param onChoiceListener code to be executed at the end of this turn.
     */
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

    /**
     * Determines the message to be shown just after the text informing the move that was used. This
     * message makes clearer the understanding of the bonus or the penalty that is applied to the
     * damage as a consequence of the relationship between the types of the move and the ones of the
     * defending pokémon. There are 3 possible messages : <br>
     * <br>
     *     - <b>"It's super effective!"</b> : means that a <b>bonus</b> (i.e. <b>typeFactor > 1</b>) was applied.<br>
     *     - <b>"It's not effective..."</b> : means that a <b>penalty</b> (i.e. <b>typeFactor < 1</b>) was applied.<br>
     *     - <b>"It has no effect..."</b> : means that there is <b>no damage</b> (i.e. <b>typeFactor = 0</b>).<br>
     * @param typeFactor the factor due to the relationship between the type of the move and the types
     *                   of the defending pokémon (computed by <b>computeTypeFactor</b>).
     * @return the message which is shown for the players so as to give a friendly feedback for them.
     */
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

    /**
     * Computes a factor derived from the relationship between the <b>type of the inflicted move</b>
     * and the <b>types of the pokémon that receives it</b>. This factor is initially 1. We
     * distinguish 3 cases : <br>
     *  <br>
     *      - If the type of the move is <b>effective</b> against one of the pokémon's type, the factor
     *  is multiplied by 2.<br>
     *      - If the type of the move is <b>not effective</b> against one of the pokémon's type, the
     *  factor is multiplied by 0.5.<br>
     *      - If the type of the move has <b>no effect</b> against one of the pokémon's type, the factor
     *  is multiplied by 0 (that is, it causes no damage at all).<br>
     *  <br>
     * The damage of the move must be multiplied by this factor afterwards in order to reflect the
     * strategy of the player when choosing the type of move considering the types of the foe's pokémon .
     * @param defendingPokemonTypes types of the pokémon that receives the attack.
     * @param effectiveTypes types against which the type of the attacking pokémon is effective.
     * @param notEffectiveTypes types against which the type of the attacking pokémon is not effective.
     * @param noEffectType types against which the type of the attacking pokémon is ineffective.
     * @return a factor resultant from the effectiveness of the inflicted move's type against the types
     * of the defending pokémon.
     */
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

    @Override
    protected void onResume() {
        super.onResume();
        mp.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    interface OnChoiceListener {
        void onChoice();
    }

}