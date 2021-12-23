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
    private List<ImageView> pokeballs = new ArrayList<>();
    private ImageView pokemonImageView;
    private boolean isLoading = false;
    private boolean isFlinched = false;
    private int nbOfTurnsTrapped = 0;
    private int remainingPokemon = 6;

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

    public void setPokemonImageView(ImageView pokemonImageView) {
        this.pokemonImageView = pokemonImageView;
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
            this.pokemonImageView.setImageResource(imageId);
        }else{
            this.pokemonImageView.setImageResource(0);
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

    public int getNbOfTurnsTrapped() {
        return nbOfTurnsTrapped;
    }

    public void setNbOfTurnsTrapped(int nbOfTurnsTrapped) {
        this.nbOfTurnsTrapped = nbOfTurnsTrapped;
    }

    /**
     * Reset some trainer attributes (isLoading, isFlinched and nbOfTurnsTrapped) when the current
     * pokémon faints
     */
    public void reset(){
        this.isLoading = false;
        this.isFlinched = false;
        this.nbOfTurnsTrapped = 0;
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
            this.nbOfTurnsTrapped = getDistinctRandomIntegers(4,5,1).get(0);
        }
    }

    public boolean receiveWrappingDamage(List<Pokemon> allPokemon){
        if (getNbOfTurnsTrapped() > 0 && currentPokemon.getPokemonServer().getFHp() > 0){
            for (Pokemon pokemon : allPokemon){
                if (pokemon.getFId().equals(currentPokemon.getPokemonServer().getFId())){
                    int fullHp = pokemon.getFHp();
                    this.getCurrentPokemon().getPokemonServer().setFHp((this.getCurrentPokemon().getPokemonServer().getFHp() - fullHp/8));
                    Log.i(TAG,"DAMAGE WRAPPED : "+(fullHp/8));
                    break;
                }
            }
            this.nbOfTurnsTrapped--;
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

    private boolean moveFlinchesOpponent(Move move) {
        double flinchingProbability = move.getFFlinchingProbability();
        double random = Math.random()*100;
        Log.i(TAG,"RANDOM FOR FLINCHING:"+random);

        return (random < flinchingProbability);
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

    public void setHpBar(Context context, List<Pokemon> allPokemon) {
        long fullHp = 100;
        for (Pokemon pokemon : allPokemon){
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
        ObjectAnimator animation = ObjectAnimator.ofInt(this.currentPokemonProgressBarHP, "progress", hp);
        animation.setDuration(3000);
        animation.setInterpolator(new LinearInterpolator());
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
     * Updates the UI (pokéballs below the HP bar) and the number of remaining pokémon
     */
    public void updateRemainingPokemon(){
        this.pokeballs.get(remainingPokemon-1).setImageResource(R.drawable.pokeball_defeated);
        this.remainingPokemon--;
        Log.i(TAG,"REMAINING POKEMON : "+this.remainingPokemon);
    }

    public enum Position{

        FRONT(""),
        BACK("_back"),
        DEFEATED(null);

        private final String imageNameSuffix;

        Position(String imageNameSuffix){
            this.imageNameSuffix = imageNameSuffix;
        }

        public String getImageNameSuffix() {
            return imageNameSuffix;
        }
    }

}
