package com.example.pokemonapp.async_task;

import static com.example.pokemonapp.util.GeneralTools.getDistinctRandomIntegers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pokemonapp.R;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask to perform the choice of a move by the CPU. This choice may be completely random or
 * "smart" depending on the gameLevel (random for easy level and "smart" otherwise). The "smart"
 * choice analyses the type of the moves available, the type of the defending pokémon and the type
 * of the attacking pokémon in order not only to provoke the maximum damage, but also taking into
 * account other factors such as the accuracy (probability of hitting the opponent), the number of
 * turns necessary to load the move or to reload after using the move etc (check comments for more
 * details).
 */
public class CpuMoveSelectionTask extends AsyncTask<Void,Void, Move> {

    private Context context;
    private List<Move> moves;
    private Pokemon currentPlayerPokemon;
    private Pokemon currentCpuPokemon;
    private String gameLevel;
    private OnResultListener<Move> onResultListener;
    private String TAG = "CpuMoveSelectionTask";

    public CpuMoveSelectionTask(Context context, List<Move> moves, Pokemon currentPlayerPokemon, Pokemon currentCpuPokemon,
                                String gameLevel, OnResultListener<Move> onResultListener) {
        this.context = context;
        this.moves = moves;
        this.currentPlayerPokemon = currentPlayerPokemon;
        this.currentCpuPokemon = currentCpuPokemon;
        this.gameLevel = gameLevel;
        this.onResultListener = onResultListener;
    }

    @Override
    protected Move doInBackground(Void... voids) {
        PokemonTypeDAO pokemonTypeDAO = PokemonAppDatabase.getInstance(context).getPokemonTypeDAO();
        MoveTypeDAO moveTypeDAO = PokemonAppDatabase.getInstance(context).getMoveTypeDAO();
        TypeEffectiveDAO typeEffectiveDAO = PokemonAppDatabase.getInstance(context).getTypeEffectiveDAO();
        TypeNotEffectiveDAO typeNotEffectiveDAO = PokemonAppDatabase.getInstance(context).getTypeNotEffectiveDAO();
        TypeNoEffectDAO typeNoEffectDAO = PokemonAppDatabase.getInstance(context).getTypeNoEffectDAO();

        int indexChosenMove = 0;

        if (!gameLevel.equals(context.getString(R.string.easy_level))){ // artificial intelligence for game levels different from easy
                                        // gets the types of the attacking pokémon
            List<Type> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemon(currentCpuPokemon.getFId());
            Type attackingPokemonType1 = attackingPokemonTypes.get(0);
            Type attackingPokemonType2 = null;
            if (attackingPokemonTypes.size() == 2){ // get type 2 only if it exists
                attackingPokemonType2 = attackingPokemonTypes.get(1);
            }

            // gets the types of the defending pokémon
            List<Type> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemon(currentPlayerPokemon.getFId());
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
                if (move.getFUserFaints() && currentCpuPokemon.getFHp() > 100){
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

        return moves.get(indexChosenMove);
    }

    @Override
    protected void onPostExecute(Move move) {
        super.onPostExecute(move);
        onResultListener.onResult(move);
    }

}
