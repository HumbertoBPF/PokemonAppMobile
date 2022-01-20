package com.example.pokemonapp.async_task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask to determine the <b>stab and type bonus</b> of a move.<br>
 * The <b>stab bonus</b> is a factor of 1.5 over the damage of a move whose type is the same as the pokémon
 * using it.<br>
 * The <b>type bonus</b> depends on the relationship between the type of the move and the type of the pokémon
 * hit. The damage is multiplied by 2 for each pokémon type against which the move type is effective.
 * For each pokémon type against which the move type is not effective, the damage is multiplied by 1/2.
 * If there is at least one type of the pokémon which is no affected by the type of the move, then the
 * move causes no damage at all.
 */
public class TypeBonusTask extends AsyncTask<Void,Void, List<Double>> {

    private Context context;
    private Pokemon attackingPokemon;
    private Pokemon defendingPokemon;
    private Move move;
    private OnResultListener<List<Double>> onResultListener;
    private final String TAG = "TypeBonusTask";

    public TypeBonusTask(Context context, Pokemon attackingPokemon, Pokemon defendingPokemon, Move move,
                         OnResultListener<List<Double>> onResultListener) {
        this.context = context;
        this.attackingPokemon = attackingPokemon;
        this.defendingPokemon = defendingPokemon;
        this.move = move;
        this.onResultListener = onResultListener;
    }

    @Override
    protected List<Double> doInBackground(Void... voids) {
        MoveTypeDAO moveTypeDAO = PokemonAppDatabase.getInstance(context).getMoveTypeDAO();
        PokemonTypeDAO pokemonTypeDAO = PokemonAppDatabase.getInstance(context).getPokemonTypeDAO();
        TypeEffectiveDAO typeEffectiveDAO = PokemonAppDatabase.getInstance(context).getTypeEffectiveDAO();
        TypeNotEffectiveDAO typeNotEffectiveDAO = PokemonAppDatabase.getInstance(context).getTypeNotEffectiveDAO();
        TypeNoEffectDAO typeNoEffectDAO = PokemonAppDatabase.getInstance(context).getTypeNoEffectDAO();

        Long moveType = moveTypeDAO.getTypesOfMove(move.getFId()).get(0).getFId();
        List<Long> attackingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(attackingPokemon.getFId());
        List<Long> defendingPokemonTypes = pokemonTypeDAO.getTypesOfPokemonIds(defendingPokemon.getFId());

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

        // if the attacking pokémon has the same type of the move that is inflicted, the move
        // get a damage bonus of 50%
        Double stab = attackingPokemonTypes.contains(moveType) ? 1.5 : 1.0;
        // factor due to the effectiveness of the type of the move against the ones of the defending pokémon
        Double typeFactor = computeTypeFactor(defendingPokemonTypes, effectiveTypes, notEffectiveTypes, noEffectType);

        List<Double> objects = new ArrayList<>();
        objects.add(stab);
        objects.add(typeFactor);

        return objects;
    }

    @Override
    protected void onPostExecute(List<Double> typeBonus) {
        super.onPostExecute(typeBonus);
        onResultListener.onResult(typeBonus);
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

}
