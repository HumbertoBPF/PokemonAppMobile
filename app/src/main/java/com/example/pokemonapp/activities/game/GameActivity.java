package com.example.pokemonapp.activities.game;

import static com.example.pokemonapp.util.DialogTools.dualButtonDialog;
import static com.example.pokemonapp.util.GeneralTools.getDistinctRandomIntegers;
import static com.example.pokemonapp.util.GeneralTools.getOverallPointsOfTeam;
import static com.example.pokemonapp.util.SharedPreferencesTools.loadTeam;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.async_task.OnTaskListener;
import com.example.pokemonapp.async_task.TypeBonusTask;
import com.example.pokemonapp.dao.ScoreDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Score;
import com.example.pokemonapp.models.Cpu;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.models.Player;
import com.example.pokemonapp.models.Trainer;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private ScoreDAO scoreDAO;

    private Player player;
    private Cpu cpu;

    private MediaPlayer mp;
    private long battleDuration = 0;
    private long gameResumedLastTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_layout);

        player = new Player(loadTeam(this,getString(R.string.filename_json_player_team)));
        cpu = new Cpu(loadTeam(this,getString(R.string.filename_json_cpu_team)));

        getLayoutElements();
        scoreDAO = PokemonAppDatabase.getInstance(this).getScoreDAO();

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

        cpu.pickPokemon(this,gameDescription);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                player.pickPokemon(getApplicationContext(), gameDescription, playerChoicesRecyclerView, GameActivity.this::battle);
            }
        }, 6000);
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

    private void battle() {
        Log.i(TAG,"Pokemon player HP : "+player.getCurrentPokemon().getCurrentHp());
        Log.i(TAG,"CPU player HP : "+cpu.getCurrentPokemon().getCurrentHp());
        player.setFlinched(false);
        cpu.setFlinched(false);
        if (areBothPokemonAlive()){
            player.pickMove(getApplicationContext(), gameDescription, playerChoicesRecyclerView, new OnTaskListener() {
                @Override
                public void onTask() {
                    cpu.pickMove(getApplicationContext(), player, gameLevel ,new OnTaskListener() {
                        @Override
                        public void onTask() {
                            Log.i(TAG,"Pokemon player speed : "+player.getCurrentPokemon().getPokemonServer().getFSpeed());
                            Log.i(TAG,"Pokemon CPU speed : "+cpu.getCurrentPokemon().getPokemonServer().getFSpeed());
                            // according to the speed attribute of the pokémon, we decide which pokémon attacks first
                            if (player.getCurrentPokemon().getPokemonServer().getFSpeed()<cpu.getCurrentPokemon().getPokemonServer().getFSpeed()){
                                attack(cpu,player,new OnTaskListener() {
                                    @Override
                                    public void onTask() {
                                        if (areBothPokemonAlive()){
                                            attack(player,cpu,GameActivity.this::battle);
                                        }
                                    }
                                });
                            }else{
                                attack(player,cpu, new OnTaskListener() {
                                    @Override
                                    public void onTask() {
                                        if (areBothPokemonAlive()){
                                            attack(cpu,player,new OnTaskListener() {
                                                @Override
                                                public void onTask() {
                                                    handler.postDelayed(GameActivity.this::battle,3000);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * Verifies if player's and foe's pokémon are alive and if not a new pokémon is picked.
     * @return a boolean indicating if player's and foe's pokémon are alive.
     */
    private boolean areBothPokemonAlive() {
        if (player.isPokemonAlive() && cpu.isPokemonAlive()){
            return true;
        }else if (!player.isPokemonAlive()){
            player.onPokemonDefeat(getApplicationContext(), gameDescription,
                    GameActivity.this::pickAnotherPlayerPokemonOrEndGame);
        }else if (!cpu.isPokemonAlive()){
            cpu.onPokemonDefeat(getApplicationContext(), gameDescription,
                    GameActivity.this::pickAnotherCpuPokemonOrEndgame);
        }

        return false;
    }

    /**
     * Decides what to do after the defeat of a CPU's pokémon (finish the game or select another
     * pokémon).
     */
    private void pickAnotherCpuPokemonOrEndgame() {
        if (!cpu.getPokemonChosenByCPU().isEmpty()){ // if the CPU still has pokémon to use, pick one and battle again
            Log.i(TAG,"CPU changes its pokemon");

            // we reset some attributes of the pokémon that was defeated
            cpu.reset();
            // the nbOfTurnsTrapped of the pokémon that stays on the field is set to 0 since the pokémon
            // which was trapping it was defeated
            player.setNbTurnsTrapped(0);

            cpu.pickPokemon(this, gameDescription);
            handler.postDelayed(this::battle,6000);
        }else{  // otherwise, finish game
            Log.i(TAG,"CPU was defeated");
            endGame(player);
        }
    }

    /**
     * Decides what to do after the defeat of a player's pokémon (finish the game or select another
     * pokémon).
     */
    private void pickAnotherPlayerPokemonOrEndGame() {
        if (player.getNbOfRemainingPokemon() > 0){   // if the player still has pokémon to use, pick one and battle again
            Log.i(TAG,"Player changes its pokemon");

            // we reset some attributes of the pokémon that was defeated
            player.reset();
            // the nbOfTurnsTrapped of the pokémon that stays on the field is set to 0 since the pokémon
            // which was trapping it was defeated
            cpu.setNbTurnsTrapped(0);

            player.pickPokemon(this, gameDescription, playerChoicesRecyclerView, this::battle);
        }else{  // otherwise, finish game
            Log.i(TAG,"Player was defeated");
            endGame(cpu);
        }
    }

    /**
     * Shows the result to the player in the TextView on the bottom of the screen and shows a play
     * again dialog.
     * @param winner Trainer object corresponding to the winner.
     */
    private void endGame(Trainer winner) {
        String resultString = winner.getTrainerName() + " " + getString(R.string.won);
        gameDescription.setText(resultString);
        if (winner instanceof Player){
            scoreDAO.getMaxScoreTask(new OnResultListener<Long>() {
                @Override
                public void onResult(Long maxScore) {
                    long scoreValue = saveScore().getScoreValue();    // saves score only when the player wins
                    String scoreMessage = "";
                    if (maxScore != null) {
                        if (scoreValue > maxScore) {   // if the new score is greater than the current max, it is a record
                            scoreMessage = getString(R.string.new_record_score_message) + scoreValue + getString(R.string.points) + "! ";
                        }else{  // else, show only the score without the message 'new record'
                            scoreMessage = getString(R.string.score_message) + scoreValue + getString(R.string.points) + ". ";
                        }
                    }else{  // if maxScoreValue is null, then there is no score register in the DB at all, so the new score
                        // is the first one and as a consequence it is a record
                        scoreMessage = getString(R.string.new_record_score_message) + scoreValue + getString(R.string.points) + "! ";
                    }
                    showEndGameDialogWithAudio(R.raw.success,resultString,scoreMessage);
                }
            }).execute();
        }else{
            showEndGameDialogWithAudio(R.raw.fail,resultString,"");
        }
    }

    /**
     * Show Endgame dialog playing a specified audio resource.
     * @param idAudio id of the audio resource to be played.
     * @param titleDialog text for the title of the dialog.
     * @param scoreMessage message informing the score to be shown before the message asking if the user wants to play again in
     *                     the body of the dialog.
     */
    private void showEndGameDialogWithAudio(int idAudio, String titleDialog, String scoreMessage) {
        // show endgame dialog
        endGameDialog = dualButtonDialog(GameActivity.this, titleDialog,
                scoreMessage +getString(R.string.play_again_dialog_text), getString(R.string.yes), getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        endGameDialog.dismiss();
                        startActivity(new Intent(GameActivity.this,PokemonSelectionActivity.class));
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
        mp = MediaPlayer.create(getApplicationContext(), idAudio);
        mp.start();
        endGameDialog.show();
    }

    private void attack(Trainer attackingTrainer, Trainer defendingTrainer, OnTaskListener onTaskListener){
        InGamePokemon attackingPokemon = attackingTrainer.getCurrentPokemon();
        InGamePokemon defendingPokemon = defendingTrainer.getCurrentPokemon();
        Move move = attackingTrainer.getCurrentMove();
        new TypeBonusTask(this, attackingPokemon.getPokemonServer(), defendingPokemon.getPokemonServer(), move,
                new OnResultListener<List<Double>>() {
                    @Override
                    public void onResult(List<Double> typeBonus) {
                        double stab = typeBonus.get(0);
                        double typeFactor = typeBonus.get(1);

                        // message to be shown as a feedback about the typeFactor
                        String messageEffectiveness = getMessageEffectiveness(typeFactor);

                        int minHits = move.getFMinTimesPerTour();
                        int maxHits = move.getFMaxTimesPerTour();

                        int nbOfHits = getDistinctRandomIntegers(minHits,maxHits,1).get(0);

                        // skip the turn, if the move needs to be loaded or if the attacking pokémon is reloading or flinched
                        if (isLoading(attackingTrainer) || isReloading(attackingTrainer) || isFlinched(attackingTrainer)){
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finishAttackTurn(defendingTrainer, onTaskListener);
                                }
                            },3000);
                            return;
                        }

                        // shows to the player the move that is going to be used
                        gameDescription.setText(attackingTrainer.getTrainerName() + "'s " +
                                attackingTrainer.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.used) +
                                attackingTrainer.getCurrentMove().getFName());

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hit(defendingTrainer, move, stab, typeFactor, messageEffectiveness,1, nbOfHits, onTaskListener);
                            }
                        }, 3000);
                    }
                }).execute();
    }

    /**
     * @param attackingTrainer Trainer object corresponding to the trainer whose pokémon attacked this turn.
     * @return a boolean indicating if this turn should be skipped to loading the move.
     */
    private boolean isLoading(Trainer attackingTrainer) {
        if (attackingTrainer.getCurrentMove().getFRoundsToLoad() == 1){
            if (!attackingTrainer.isLoading()){       // no need to set to load if pokémon is already loading
                if (attackingTrainer instanceof Player){    // set to true, because in the this turn the trainer will not be able to attack
                    player.setLoading(true);
                }else{
                    cpu.setLoading(true);
                }
                gameDescription.setText(attackingTrainer.getTrainerName() + "'s " +
                        attackingTrainer.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.loads_attack));
                return true;
            }
        }
        return false;
    }

    /**
     * @param attackingTrainer Trainer object corresponding to the trainer whose pokémon attacked this turn.
     * @return a boolean indicating if this turn should be skipped to reload the move.
     */
    private boolean isReloading(Trainer attackingTrainer) {
        if (attackingTrainer.getCurrentMove().getFRoundsToLoad() == -1){
            if (attackingTrainer.isLoading()){
                if (attackingTrainer instanceof Player){    // set to false, because in the next turn the trainer will be able to pick a move
                    player.setLoading(false);
                }else{
                    cpu.setLoading(false);
                }
                gameDescription.setText(attackingTrainer.getTrainerName() + "'s " +
                        attackingTrainer.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.is_reloading));
                return true;
            }
        }
        return false;
    }

    /**
     * @param attackingTrainer Trainer object corresponding to the trainer whose pokémon attacked this turn.
     * @return a boolean indicating if this turn should be skipped because the attacking pokémon is flinched.
     */
    private boolean isFlinched(Trainer attackingTrainer) {
        if (attackingTrainer.isFlinched()){
            gameDescription.setText(attackingTrainer.getTrainerName() + "'s " +
                    attackingTrainer.getCurrentPokemon().getPokemonServer().getFName() + getString(R.string.is_flinched));
            return true;
        }
        return false;
    }

    private void hit(Trainer defendingTrainer, Move move, double stab, double typeFactor,
                     String messageEffectiveness, int currentHit, int nbOfHits, OnTaskListener onTaskListener) {

        if (defendingTrainer.getCurrentPokemon().getCurrentHp() <= 0){
            onTaskListener.onTask();
        }else{
            double damage;
            if (defendingTrainer instanceof Cpu){
                damage = player.hitOpponent(currentHit,move,stab,typeFactor); // gets the damage
                if (damage == -1){ // defending pokémon processes the move received and the UI is updated according to the result
                    gameDescription.setText(R.string.attack_missed_msg);
                }else{
                    gameDescription.setText(messageEffectiveness);
                    cpu.receiveDamage(damage,move);
                }
            }else{
                damage = cpu.hitOpponent(currentHit,move,stab,typeFactor);
                if (damage == -1){
                    gameDescription.setText(R.string.attack_missed_msg);
                }else{
                    gameDescription.setText(messageEffectiveness);
                    player.receiveDamage(damage,move);
                }
            }

            // updates HP bar (for the case when the pokémon faints after using a move, maybe it
            // can be done in a better way)
            player.setHpBar(this);
            cpu.setHpBar(this);

            hitAgainOrStop(defendingTrainer, move, stab, typeFactor, messageEffectiveness, currentHit, nbOfHits, onTaskListener);
        }
    }

    /**
     * Decides if the move must hit the opponent again or if the turn is finished.
     * @param move move that is being used.
     * @param stab stab factor (bonus of 50% over damage when the type of the attack matches the type of the user).
     * @param typeFactor type factor derived from the relationship between the type of the move and the types of defendingPokémon.
     * @param messageEffectiveness message reflecting the effectiveness of the move.
     * @param currentHit number of times that this move has already touched the opponent this turn + 1.
     * @param nbOfHits number of hits that this move is intended to touch the opponent this turn.
     * @param onTaskListener code to be executed at the end of the current turn.
     */
    private void hitAgainOrStop(Trainer defendingTrainer, Move move, double stab, double typeFactor,
                                String messageEffectiveness, int currentHit, int nbOfHits, OnTaskListener onTaskListener) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (nbOfHits > 1){ // if this move must hit more than once, we keep hitting until reaching the number of hits
                    gameDescription.setText(getString(R.string.hit)+ currentHit +getString(R.string.times));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (currentHit < nbOfHits){
                                hit(defendingTrainer, move, stab, typeFactor, messageEffectiveness, currentHit +1, nbOfHits, onTaskListener);
                            }else if (currentHit == nbOfHits){
                                finishAttackTurn(defendingTrainer, onTaskListener);
                            }
                        }
                    },3000);
                }else{ // else we can finish the turn
                    finishAttackTurn(defendingTrainer, onTaskListener);
                }
            }
        },3000);
    }

    /**
     * Finishes the turn by inflicting wrapping damage on the opponent and executing the code specified
     * in the listener parameter.
     * @param defendingTrainer trainer whose pokémon was attacked this turn.
     * @param onTaskListener code to be executed at the end of this turn.
     */
    private void finishAttackTurn(Trainer defendingTrainer, OnTaskListener onTaskListener) {
        int delay = 0;
        if (defendingTrainer instanceof Cpu) {
            if (cpu.receiveWrappingDamage()) {
                gameDescription.setText(cpu.getTrainerName() + "'s " + cpu.getCurrentPokemon().getPokemonServer().getFName() +
                        getString(R.string.trapping_damage));
                delay = 3000;
            }
        } else {
            if (player.receiveWrappingDamage()) {
                gameDescription.setText(player.getTrainerName() + "'s " + player.getCurrentPokemon().getPokemonServer().getFName() +
                        getString(R.string.trapping_damage));
                delay = 3000;
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                player.setHpBar(getApplicationContext());
                cpu.setHpBar(getApplicationContext());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onTaskListener.onTask();
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
        gameResumedLastTime = Calendar.getInstance().getTimeInMillis();
        Log.i(TAG,"TimeManagement gameResumedLastTime : "+gameResumedLastTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mp.pause();
        incrementBattleDuration();
    }

    /**
     * Saves the score and all the information associated.
     * @return the score that was stored.
     */
    private Score saveScore(){
        incrementBattleDuration();  // increments the class battleDuration for the last time

        // gets the overall points of player's team and of the cpu's team
        long overallPointsPlayer = getOverallPointsOfTeam(loadTeam(this,getString(R.string.filename_json_player_team)));
        long overallPointsCpu = getOverallPointsOfTeam(loadTeam(this,getString(R.string.filename_json_cpu_team)));
        int nbRemainingPokemon = player.getNbOfRemainingPokemon();   // gets number of remaining player's pokémon

        // gets the JSON corresponding to player's and cpu's team
        SharedPreferences sh = getSharedPreferences(getString(R.string.name_shared_preferences_file), MODE_PRIVATE);
        String playerTeam = sh.getString(getString(R.string.filename_json_player_team),"");
        String cpuTeam = sh.getString(getString(R.string.filename_json_cpu_team),"");

        // gets the current date string (time, day, month and year)
        String date = DateFormat.getInstance().format(new Date());

        Score score = new Score(getApplicationContext(), battleDuration, nbRemainingPokemon, overallPointsPlayer,
                overallPointsCpu, gameMode, gameLevel, playerTeam, cpuTeam, date);

        // async task to store the score entity in the local DB
        scoreDAO.saveTask(score, new OnTaskListener() {
            @Override
            public void onTask() {

            }
        }).execute();

        return score;
    }

    /**
     * When the game is paused or ended, it increment the class variable <b>battleDuration</b> with
     * the time between this pause and the last time that the game was started.
     */
    private void incrementBattleDuration() {
        long pauseTime = Calendar.getInstance().getTimeInMillis();
        Log.i(TAG,"TimeManagement pauseTime : "+pauseTime);
        if (pauseTime > gameResumedLastTime){
            battleDuration += pauseTime - gameResumedLastTime;
            Log.i(TAG,"TimeManagement battleDuration so far : "+battleDuration);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

}