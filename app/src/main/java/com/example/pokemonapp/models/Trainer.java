package com.example.pokemonapp.models;

import static com.example.pokemonapp.util.GeneralTools.getDistinctRandomIntegers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.OnTaskListener;
import com.example.pokemonapp.entities.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to gather and to treat information concerning the player and the cpu. Its attributes
 * are :<br>
 * <br>
 *      - <b>team</b> : list of pokémon of the trainer.<br>
 *      - <b>currentPokemon</b> : pokémon that is on the field.<br>
 *      - <b>currentMove</b> : move that was choose lastly.<br>
 *      - <b>currentPokemonName</b> : TextView with the name of the current pokémon.<br>
 *      - <b>currentPokemonProgressBarHP</b> : ProgressBar representing the HP of the current
 *      pokémon.<br>
 *      - <b>currentPokemonImageView</b> : ImageView with the image of the current pokémon.<br>
 *      - <b>pokeballs</b> : list of ImageViews with pokéballs to represent the remaining pokémon
 *      of the trainer.<br>
 *      - <b>nbRemainingPokemon</b> : integer indicating the number of remaining pokémon (i.e.
 *      pokémon that can still be used in the current battle).<br>
 *      - <b>isLoading</b> : boolean indicating if the pokémon is loading an attack.<br>
 *      - <b>isFlinched</b> : boolean indicating if the pokémon is flinched during this round.<br>
 *      - <b>nbTurnsTrapped</b> : integer indicating the number of turns during which the current
 *      pokémon will keep trapped.<br>
 *      - <b>trainerName</b> : String indicating the name that will be used to refer to this player
 *      in the text of the game.<br>
 */
public abstract class Trainer {

    private final String TAG = "GameActivity";
    private List<InGamePokemon> team;
    private InGamePokemon currentPokemon;
    private Move currentMove;
    private TextView currentPokemonName;
    private ProgressBar currentPokemonProgressBarHP;
    protected ImageView currentPokemonImageView;
    private List<ImageView> pokeballs = new ArrayList<>();
    private int nbRemainingPokemon = 6;
    private boolean isLoading = false;
    private boolean isFlinched = false;
    private int nbTurnsTrapped = 0;
    protected Handler handler = new Handler();
    protected String trainerName;

    public Trainer(List<InGamePokemon> team) {
        this.team = team;
    }

    public List<InGamePokemon> getTeam() {
        return team;
    }

    public InGamePokemon getCurrentPokemon() {
        return currentPokemon;
    }

    public void setCurrentPokemon(InGamePokemon currentPokemon) {
        this.currentPokemon = currentPokemon;
    }

    public Move getCurrentMove() {
        return currentMove;
    }

    public void setCurrentMove(Move currentMove) {
        this.currentMove = currentMove;
    }

    public void setCurrentPokemonName(TextView currentPokemonName) {
        this.currentPokemonName = currentPokemonName;
    }

    public void setCurrentPokemonProgressBarHP(ProgressBar currentPokemonProgressBarHP) {
        this.currentPokemonProgressBarHP = currentPokemonProgressBarHP;
    }

    public void setCurrentPokemonImageView(ImageView currentPokemonImageView) {
        this.currentPokemonImageView = currentPokemonImageView;
    }

    public void addPokeball(ImageView pokeball) {
        this.pokeballs.add(pokeball);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        this.isLoading = loading;
    }

    public boolean isFlinched() {
        return isFlinched;
    }

    public void setFlinched(boolean flinched) {
        this.isFlinched = flinched;
    }

    public void setNbTurnsTrapped(int nbTurnsTrapped) {
        this.nbTurnsTrapped = nbTurnsTrapped;
    }

    public String getTrainerName() {
        return trainerName;
    }

    /**
     * Reset some trainer attributes (isLoading, isFlinched and nbOfTurnsTrapped) when the current
     * pokémon faints.
     */
    public void reset(){
        this.isLoading = false;
        this.isFlinched = false;
        this.nbTurnsTrapped = 0;
    }

    public boolean isPokemonAlive(){
        return getCurrentPokemon().getCurrentHp()>0;
    }

    /**
     * Determines if the attack hits or misses and, for the first case, determines the damage
     * inflicted.
     * @param currentHit number of times that this move has already hit the opponent so far + 1.
     * @param move move is being used.
     * @param stab stab factor (1.5 if there is stab, 1.0 otherwise).
     * @param typeFactor type factor (greater than 1 if the move is super effective, between 0 and 1.
     *                   if it is not effective or 0 if it has no effect).
     * @return the damage inflicted if the move hits the opponent or -1 if it misses.
     */
    public double hitOpponent(int currentHit, Move move, double stab, double typeFactor){
        // updates the number of PP when the first hit is processed (because all the moves hit at least once)
        if (currentHit == 1){
            this.getCurrentPokemon().setMoves(updatePPs(this.getCurrentPokemon(), move));
        }

        if (!attackMissed(move)) {
            double randomFactor = Math.random()*0.15 + 0.85;
            double attackStat;
            double defenseStat;

            // decides if the attack and defense stats should be special ou physical
            if (move.getFCategory().equals("Special")){
                attackStat = this.getCurrentPokemon().getPokemonServer().getFSpAttack();
                defenseStat = this.getCurrentPokemon().getPokemonServer().getFSpDefense();
            }else{
                attackStat = this.getCurrentPokemon().getPokemonServer().getFAttack();
                defenseStat = this.getCurrentPokemon().getPokemonServer().getFDefense();
            }

            Log.i(TAG, "RANDOM FACTOR:" + randomFactor + " " + "STAB:" + stab + " " + "TYPE_FACTOR:" + typeFactor);
            // formula to compute the damage when the move hits the opponent
            double damage = ((42 * move.getFPower() * (attackStat / defenseStat)) / 50 + 2) * randomFactor * stab * typeFactor;

            if (this.isLoading()){  // if pokémon was loading the move, it does not need to keep loading
                this.setLoading(false);
            }

            if (move.getFUserFaints()){ // sets HP to 0 if the user of the move must faint after using it
                this.getCurrentPokemon().setCurrentHp(0);
            }

            if (move.getFRoundsToLoad() == -1){ // if move requires reloading, set the pokémon to do it
                this.setLoading(true);
            }

            if (move.getFRecoversHp()){ // if the move recovers the HP of the user, half of the damage is added to the HP or it is recovered
                                        // completely if the original HP is lower then the result of this addition
                int recoveredHp = this.getCurrentPokemon().getCurrentHp() + ((int) damage/2);
                int maxHp = this.getCurrentPokemon().getPokemonServer().getFHp();
                if (recoveredHp > maxHp){
                    recoveredHp = maxHp;
                }
                this.getCurrentPokemon().setCurrentHp(recoveredHp);
            }

            return damage;
        }else{
            return -1; // if -1 is returned, the pokémon misses the attack
        }
    }

    /**
     * Processes a move inflicted by the opponent. This method updates UI (HP bar) and some attributes
     * of the pokémon (flinched and trapped status).
     * @param damage damage received.
     * @param move move inflicted by the opponent.
     */
    public void receiveDamage(double damage, Move move){
        Log.i(TAG,"ATTACK DAMAGE : "+damage);
        // updates HP bar
        this.getCurrentPokemon().setCurrentHp((int) (this.getCurrentPokemon().getCurrentHp() - damage));
        if (moveFlinchesOpponent(move)){    // if move flinches, set the pokémon as flinched
            this.setFlinched(true);
        }
        if (move.getFTrapping()){   // if move traps, set the pokémon trapped for 4 or 5 turns
            this.nbTurnsTrapped = getDistinctRandomIntegers(4,5,1).get(0);
        }
    }

    /**
     * Inflicts wrapping damage if necessary. Wrapping damage reduces the current HP of a pokémon by
     * 1/8 of the total HP.
     * @return true if wrapping damage was inflicted, false otherwise.
     */
    public boolean receiveWrappingDamage(){
        if (this.nbTurnsTrapped > 0 && currentPokemon.getCurrentHp() > 0){
            int fullHp = currentPokemon.getPokemonServer().getFHp();
            this.getCurrentPokemon().setCurrentHp((this.getCurrentPokemon().getCurrentHp() - fullHp/8));
            Log.i(TAG,"DAMAGE WRAPPED : "+(fullHp/8));
            this.nbTurnsTrapped--;
            return true;
        }

        return false;
    }

    /**
     * Decrement the PPs of a move.
     * @param inGamePokemon InGamePokemon that used the specified move.
     * @param move move whose PPs must be decremented.
     * @return new list of moves of the InGamePokemon (with the PPs of the specified move decremented).
     */
    @NonNull
    private List<Move> updatePPs(InGamePokemon inGamePokemon, Move move) {
        List<Move> moves = inGamePokemon.getMoves();
        for (Move m : moves){
            if (m.getFId().equals(move.getFId())){
                int pp = m.getFPp()-1;
                m.setFPp(pp);
            }
        }
        return moves;
    }

    /**
     * Takes into account the move's probability of flinching to determine if the foe's pokémon will
     * be flinched. It only has any effect if the pokémon using the move that may flinch attacks first
     * since isFlinched is reset after both pokémon attack.
     * @param move move being used.
     * @return a boolean indicating if the move flinches the opponent.
     */
    private boolean moveFlinchesOpponent(Move move) {
        double flinchingProbability = move.getFFlinchingProbability();
        double random = Math.random()*100; // we get a random number between 0 and 100
        Log.i(TAG,"RANDOM FOR FLINCHING:"+random);

        return (random < flinchingProbability); // if the random is lower than the probability of flinching,
                                                // foe's pokémon is flinched
    }

    /**
     * Compares a random between 0 and 100 to the accuracy of the move concerned to determine if the
     * move missed the opponent.
     * @param move move being used.
     * @return a boolean indicating if the move missed the opponent.
     */
    private boolean attackMissed(Move move) {
        double accuracy = move.getFAccuracy();
        double random = Math.random()*100;
        Log.i(TAG,"RANDOM FOR ACCURACY:"+random);

        return (random > accuracy);
    }

    public void updateCurrentPokemonName(){
        this.currentPokemonName.setText(currentPokemon.getPokemonServer().getFName());
    }

    /**
     * Updates smoothly the value and the color of the HP bar.
     * @param context context of the activity calling this method.
     */
    public void setHpBar(Context context) {
        long fullHp = currentPokemon.getPokemonServer().getFHp();
        this.currentPokemonProgressBarHP.setMax((int) fullHp);
        Integer hp = currentPokemon.getCurrentHp();
        if (hp < 0){
            hp = 0;
        }
        // animator to smooth the transition between the old and the new value of the progress bar
        ObjectAnimator animation = ObjectAnimator.ofInt(this.currentPokemonProgressBarHP, "progress", hp);
        animation.setDuration(3000);
        animation.setInterpolator(new LinearInterpolator());
        // update the color of the hp bar according to its current value
        long finalFullHp = fullHp;
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int hpBarColor = R.color.pokemon_theme_color;
                int currentProgress = currentPokemonProgressBarHP.getProgress();
                if (currentProgress>0.5* finalFullHp){
                    hpBarColor = R.color.green_hp_bar;
                }else if (currentProgress>0.2* finalFullHp){
                    hpBarColor = R.color.moves_theme_color;
                }
                currentPokemonProgressBarHP.setProgressTintList(ColorStateList.valueOf(context.getResources().getColor(hpBarColor)));
            }
        });
        animation.start();
    }

    /**
     * Updates the UI (pokéballs below the HP bar) and the number of remaining pokémon.
     */
    public void updateRemainingPokemon(){
        this.pokeballs.get(nbRemainingPokemon -1).setImageResource(R.drawable.pokeball_defeated);
        this.nbRemainingPokemon--;
        Log.i(TAG,"REMAINING POKEMON : "+this.nbRemainingPokemon);
    }

    /**
     * Updates the UI when a CPU's pokémon is defeated.
     * @param context context of the activity calling this method.
     * @param gameDescription TextView which shows the text describing what is happening in the game.
     * @param onTaskListener OnTaskListener specifying what should be done next.
     */
    public void onPokemonDefeat(Context context, TextView gameDescription, OnTaskListener onTaskListener) {
        // updates UI
        updateRemainingPokemon();
        gameDescription.setText(trainerName + "'s " +
                getCurrentPokemon().getPokemonServer().getFName() + context.getString(R.string.fainted));
        this.currentPokemonImageView.setImageResource(0);
        // plays fainting sound
        playsFaintSound(context);
        // decides the next step
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onTaskListener.onTask();
            }
        }, 3000);
    }

    private void playsFaintSound(Context context) {
        MediaPlayer mpLocal = MediaPlayer.create(context, R.raw.faint);
        mpLocal.start();
        mpLocal.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mpLocal.release();
            }
        });
    }

    /**
     * @return number of pokémon whose HP is greater than 0 (i.e. that have not fainted yet)
     */
    public int getNbOfRemainingPokemon(){
        int nbOfRemainingPokemon = 0;
        for (InGamePokemon inGamePokemon : getTeam()){
            if (inGamePokemon.getCurrentHp() > 0){
                nbOfRemainingPokemon++;
            }
        }
        Log.i(TAG,"nbOfRemainingPokemon = "+nbOfRemainingPokemon);
        return nbOfRemainingPokemon;
    }

    /**
     * @param inGamePokemon pokémon concerned.
     * @return list of moves whose the number of pp is greater than 0.
     */
    public List<Move> getRemainingMoves(InGamePokemon inGamePokemon) {
        List<Move> moves = new ArrayList<>();
        for (Move move : inGamePokemon.getMoves()){
            if (move.getFPp() > 0){
                moves.add(move);
            }
        }
        return moves;
    }

}
