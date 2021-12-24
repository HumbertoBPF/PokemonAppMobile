package com.example.pokemonapp.models;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Trainer {

    private final String TAG = "GameActivity";
    private List<InGamePokemon> team;
    private InGamePokemon currentPokemon;
    private Move currentMove;
    private TextView currentPokemonName;
    private ProgressBar currentPokemonProgressBarHP;
    private ImageView currentPokemonImageView;
    private List<ImageView> pokeballs = new ArrayList<>();
    private int nbRemainingPokemon = 6;
    private boolean isLoading = false;
    private boolean isFlinched = false;
    private int nbTurnsTrapped = 0;

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
     */
    public Trainer() {
    }

    public List<InGamePokemon> getTeam() {
        return team;
    }

    public void setTeam(List<InGamePokemon> team) {
        this.team = team;
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

    public void setPokemonImageResource(Context context, Position position) {
        String imageNameSuffix = position.getImageNameSuffix();
        if (imageNameSuffix != null){
            String pokemonImageName = "pokemon_"+
                    getCurrentPokemon().getPokemonServer().getFName().toLowerCase(Locale.ROOT)
                            .replace("'","")
                            .replace(" ","_")
                            .replace(".","") + imageNameSuffix;
            int imageId = context.getResources().getIdentifier(pokemonImageName,"drawable",context.getPackageName());
            this.currentPokemonImageView.setImageResource(imageId);
        }else{
            this.currentPokemonImageView.setImageResource(0);
        }
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

    public int getNbTurnsTrapped() {
        return nbTurnsTrapped;
    }

    public void setNbTurnsTrapped(int nbTurnsTrapped) {
        this.nbTurnsTrapped = nbTurnsTrapped;
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
        return getCurrentPokemon().getPokemonServer().getFHp()>0;
    }

    /**
     * Determines if the attack hits or misses and, for the first case, determines the damage
     * inflicted.
     * @param defendingPokemon pokémon that is going to be hit / that will receive the attack.
     * @param currentHit number of times that this move has already hit the opponent so far + 1.
     * @param move move is being used.
     * @param stab stab factor (1.5 if there is stab, 1.0 otherwise).
     * @param typeFactor type factor (greater than 1 if the move is super effective, between 0 and 1.
     *                   if it is not effective or 0 if it has no effect).
     * @return the damage inflicted if the move hits the opponent or -1 if it misses.
     */
    public double hitOpponent(InGamePokemon defendingPokemon, int currentHit, Move move, double stab, double typeFactor){
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
                this.getCurrentPokemon().getPokemonServer().setFHp(0);
            }

            if (move.getFRoundsToLoad() == -1){ // if move requires reloading, set the pokémon to do it
                this.setLoading(true);
            }

            return damage;
        }else{
            return -1; // if -1 is returned, the pokémon missed the attack
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
        this.getCurrentPokemon().getPokemonServer().setFHp((int) (this.getCurrentPokemon().getPokemonServer().getFHp() - damage));
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
     * @param allPokemon list with all the pokémon from DB.
     * @return true if wrapping damage was inflicted, false otherwise.
     */
    public boolean receiveWrappingDamage(List<Pokemon> allPokemon){
        if (getNbTurnsTrapped() > 0 && currentPokemon.getPokemonServer().getFHp() > 0){
            for (Pokemon pokemon : allPokemon){ // iterates over the list of all pokémon to find the full HP, since we will reduce
                                                // the current HP by 1/8 of the full HP
                if (pokemon.getFId().equals(currentPokemon.getPokemonServer().getFId())){
                    int fullHp = pokemon.getFHp();
                    this.getCurrentPokemon().getPokemonServer().setFHp((this.getCurrentPokemon().getPokemonServer().getFHp() - fullHp/8));
                    Log.i(TAG,"DAMAGE WRAPPED : "+(fullHp/8));
                    break;
                }
            }
            this.nbTurnsTrapped--;
            return true;
        }

        return false;
    }

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
     * @param allPokemon list with all the pokémon of the DB.
     */
    public void setHpBar(Context context, List<Pokemon> allPokemon) {
        long fullHp = 100;
        for (Pokemon pokemon : allPokemon){ // iterates over all the pokémon from the DB to get the fullHp of the currentPokemon,
                                            // which will define the maximum of the HPBar
            if (pokemon.getFId().equals(currentPokemon.getPokemonServer().getFId())){
                fullHp = pokemon.getFHp();
                break;
            }
        }
        this.currentPokemonProgressBarHP.setMax((int) fullHp);
        Integer hp = currentPokemon.getPokemonServer().getFHp();
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
     * Enum to indicate the positions of the pokémon in the image loaded into its ImageView. These
     * positions are : <br>
     * <br>
     *     - <b>FRONT</b> : frontal image.<br>
     *     - <b>BACK</b> : back image.<br>
     *     - <b>DEFEATED</b> : no image. It is used for the moments where the pokémon still has not
     *     been chosen or where it is defeated.<br>
     */
    public enum Position{

        FRONT(""),  // frontal image of the pokémon
        BACK("_back"),  // back image of the pokémon
        DEFEATED(null); // no image

        private final String imageNameSuffix;

        Position(String imageNameSuffix){
            this.imageNameSuffix = imageNameSuffix;
        }

        public String getImageNameSuffix() {
            return imageNameSuffix;
        }
    }

}
