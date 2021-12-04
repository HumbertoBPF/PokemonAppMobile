package com.example.pokemonapp.models;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.pokemonapp.R;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;

import java.util.List;

public class Trainer {

    private String TAG = "GameActivity";
    private List<InGamePokemon> team;
    private InGamePokemon currentPokemon;
    private Move currentMove;
    private TextView currentPokemonName;
    private TextView currentPokemonHP;
    private boolean loading = false;
    private boolean flinched = false;

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

    public TextView getCurrentPokemonHP() {
        return currentPokemonHP;
    }

    public void setCurrentPokemonHP(TextView currentPokemonHP) {
        this.currentPokemonHP = currentPokemonHP;
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

    public void reset(){
        this.loading = false;
        this.flinched = false;
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
        this.getCurrentPokemon().getPokemonServer().setFHp((int) (this.getCurrentPokemon().getPokemonServer().getFHp() - damage));
        if (moveFlinchesOpponent(move)){
            this.setFlinched(true);
        }
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
        int hpBarColor = R.color.pokemon_theme_color;
        if (currentPokemon.getPokemonServer().getFHp()>0.5*fullHp){
            hpBarColor = R.color.green_hp_bar;
        }else if (currentPokemon.getPokemonServer().getFHp()>0.2*fullHp){
            hpBarColor = R.color.moves_theme_color;
        }
        currentPokemonHP.setTextColor(context.getResources().getColor(hpBarColor));
        Integer hp = currentPokemon.getPokemonServer().getFHp();
        currentPokemonHP.setText(context.getString(R.string.hp_label)+hp.toString());
        if (hp < 0){
            currentPokemonHP.setText(context.getString(R.string.hp_label)+"0");
        }
    }

}
