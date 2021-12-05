package com.example.pokemonapp.models;

import static com.example.pokemonapp.util.Tools.getDistinctRandomIntegers;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
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

public class Trainer {

    private String TAG = "GameActivity";
    private List<InGamePokemon> team;
    private InGamePokemon currentPokemon;
    private Move currentMove;
    private TextView currentPokemonName;
    private ProgressBar currentPokemonProgressBarHP;
    private List<ImageView> pokeballs = new ArrayList<>();
    private boolean loading = false;
    private boolean flinched = false;
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

    public TextView getCurrentPokemonName() {
        return currentPokemonName;
    }

    public void setCurrentPokemonName(TextView currentPokemonName) {
        this.currentPokemonName = currentPokemonName;
    }

    public ProgressBar getCurrentPokemonProgressBarHP() {
        return currentPokemonProgressBarHP;
    }

    public void setCurrentPokemonProgressBarHP(ProgressBar currentPokemonProgressBarHP) {
        this.currentPokemonProgressBarHP = currentPokemonProgressBarHP;
    }

    public List<ImageView> getPokeballs() {
        return pokeballs;
    }

    public void setPokeballs(List<ImageView> pokeballs) {
        this.pokeballs = pokeballs;
    }

    public void addPokeball(ImageView pokeball) {
        this.pokeballs.add(pokeball);
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public boolean isFlinched() {
        return flinched;
    }

    public void setFlinched(boolean flinched) {
        this.flinched = flinched;
    }

    public int getNbOfTurnsTrapped() {
        return nbOfTurnsTrapped;
    }

    public void setNbOfTurnsTrapped(int nbOfTurnsTrapped) {
        this.nbOfTurnsTrapped = nbOfTurnsTrapped;
    }

    public void reset(){
        this.loading = false;
        this.flinched = false;
        this.nbOfTurnsTrapped = 0;
    }

    public boolean isPokemonAlive(){
        return getCurrentPokemon().getPokemonServer().getFHp()>0;
    }

    // if returns -1, the pokémon missed the attack
    public double hitOpponent(InGamePokemon defendingPokemon, int currentHit, Move move, double stab, double typeFactor){
        double randomFactor = Math.random()*0.15 + 0.85;
        double attackStat = this.getCurrentPokemon().getPokemonServer().getFAttack();
        double defenseStat = move.getFCategory().equals("Special")?defendingPokemon.getPokemonServer().getFSpDefense():defendingPokemon.getPokemonServer().getFDefense();

        if (currentHit == 1){
            this.getCurrentPokemon().setMoves(updatePPs(this.getCurrentPokemon(), move));
        }
        if (!attackMissed(move)) {
            Log.i(TAG, "RANDOM FACTOR:" + randomFactor + " " + "STAB:" + stab + " " + "TYPE_FACTOR:" + typeFactor);
            double damage = ((42 * move.getFPower() * (attackStat / defenseStat)) / 50 + 2) * randomFactor * stab * typeFactor;
            if (this.isLoading()){
                this.setLoading(false);
            }
            if (move.getFUserFaints()){
                this.getCurrentPokemon().getPokemonServer().setFHp(0);
            }
            if (move.getFRoundsToLoad() == -1){
                this.setLoading(true);
            }
            return damage;
        }else{
            return -1;
        }
    }

    public void receiveDamage(double damage, Move move){
        Log.i(TAG,"ATTACK DAMAGE : "+damage);
        this.getCurrentPokemon().getPokemonServer().setFHp((int) (this.getCurrentPokemon().getPokemonServer().getFHp() - damage));
        if (moveFlinchesOpponent(move)){
            this.setFlinched(true);
        }
        if (move.getFTrapping()){
            this.nbOfTurnsTrapped = getDistinctRandomIntegers(4,5,1).get(0);
        }
    }

    public boolean receiveWrappingDamage(List<Pokemon> allPokemon){
        if (getNbOfTurnsTrapped() > 0){
            for (Pokemon pokemon : allPokemon){
                if (pokemon.getFId().equals(currentPokemon.getPokemonServer().getFId())){
                    int fullHp = pokemon.getFHp();
                    this.getCurrentPokemon().getPokemonServer().setFHp((int) (this.getCurrentPokemon().getPokemonServer().getFHp() - fullHp/8));
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

    private boolean attackMissed(Move move) {
        double accuracy = move.getFAccuracy();
        double random = Math.random()*100;
        Log.i(TAG,"RANDOM FOR ACCURACY:"+random);

        return (random > accuracy);
    }

    public void setTextHP(Context context, List<Pokemon> allPokemon) {
        currentPokemonName.setText(currentPokemon.getPokemonServer().getFName());
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

    public void countDefeatedPokemon(){
        this.pokeballs.get(remainingPokemon-1).setImageResource(R.drawable.pokeball_defeated);
        this.remainingPokemon--;
        Log.i(TAG,"REMAINING POKEMON : "+this.remainingPokemon);
    }

}
