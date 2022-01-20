package com.example.pokemonapp.async_task;

import static com.example.pokemonapp.util.GeneralTools.getDistinctRandomIntegers;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonMoveDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * AsyncTask to choose the move set of a pokémon "smartly". Some aspects are taken into account in
 * order to do a wise choice. They are :<br>
 *     - The diversity of the types of the moves such that the pokémon is less likely to be useless
 *     against pokémon of a specific type.<br>
 *     - The power of the move so as to try to pick some of the strongest moves available.<br>
 *     - A random aspect to add some unpredictability on the move set (if it was predictable, the
 *     player could develop a strategy against the cpu easier).<br>
 */
public class SmartMoveSetSelectionTask extends AsyncTask<Void,Void, List<Move>> {

    private Context context;
    private Pokemon pokemon;
    private OnResultListener<List<Move>> onResultListener;

    public SmartMoveSetSelectionTask(Context context, Pokemon pokemon, OnResultListener<List<Move>> onResultListener) {
        this.context = context;
        this.pokemon = pokemon;
        this.onResultListener = onResultListener;
    }

    @Override
    protected List<Move> doInBackground(Void... voids) {
        PokemonMoveDAO pokemonMoveDAO = PokemonAppDatabase.getInstance(context).getPokemonMoveDAO();
        PokemonTypeDAO pokemonTypeDAO = PokemonAppDatabase.getInstance(context).getPokemonTypeDAO();
        MoveTypeDAO moveTypeDAO = PokemonAppDatabase.getInstance(context).getMoveTypeDAO();

        // get list of moves of the current pokémon ordered by power
        List<Move> bestMoves = pokemonMoveDAO.getStrongestMovesOfPokemon(pokemon.getFId());

        List<Type> typesPokemon = pokemonTypeDAO.getTypesOfPokemon(pokemon.getFId());

        Type type1;
        Type type2 = null;

        type1 = typesPokemon.get(0);
        if (typesPokemon.size() > 1){
            type1 = typesPokemon.get(1);
            type2 = typesPokemon.get(0);
        }

        List<Move> movesStabType1 = new ArrayList<>();  // list for moves whose type is the primary type of the pokémon
        List<Move> movesStabType2 = new ArrayList<>();  // list for moves whose type is the secondary type of the pokémon
        List<Move> movesNoStab = new ArrayList<>();     // list for all the other moves

        for (Move move : bestMoves){    // groups moves according to their type and the type(s) of the pokémon
            Type typeMove = moveTypeDAO.getTypesOfMove(move.getFId()).get(0);
            if (typeMove.getFId().equals(type1.getFId())){
                movesStabType1.add(move);
            }else if (type2 != null){
                if (typeMove.getFId().equals(type2.getFId())){
                    movesStabType2.add(move);
                }else{
                    movesNoStab.add(move);
                }
            }else{
                movesNoStab.add(move);
            }
        }

        List<Move> selectedMoves = new ArrayList<>();   // final list of moves
        List<Integer> indexes;                      // array to store random indexes that are going to be picked
        int index;                                  // int variable to manipulate the elements of the array above

        // if there is any move with the secondary type, pick one randomly
        if (!movesStabType2.isEmpty()){
            indexes = getDistinctRandomIntegers(0,movesStabType2.size()-1,1);
            index = indexes.get(0);
            selectedMoves.add(movesStabType2.remove(index));     // the size should be 1 here in the best case
        }

        // if there is any move with the primary type, pick one randomly
        if (!movesStabType1.isEmpty()){
            indexes = getDistinctRandomIntegers(0,movesStabType1.size()-1,1);
            index = indexes.get(0);
            selectedMoves.add(movesStabType1.remove(index));     // the size should be 2 here in the best case

            // if there is no move with the secondary type, the size of the final list of moves is 1 so far. For that case,
            // if there are any remaining move of the primary type, add the strongest one to the final list
            if (selectedMoves.size() < 2){
                if (!movesStabType1.isEmpty()){
                    selectedMoves.add(movesStabType1.remove(0));
                }
            }

        }

        // if only one move was added so far, it is possible that we have only
        // one move of the type2 and no move at all of the type1. For such case,
        // we add a second stab move of the type 2(hence, we will have both stab moves of type2)
        if (selectedMoves.size() < 2){
            if (!movesStabType2.isEmpty()){
                selectedMoves.add(movesStabType2.remove(0));
            }
        }


        // at this point, we complete the list of moves with the no stab moves available
        if (!movesNoStab.isEmpty()){
            // we try to complete the list of final moves, but we have to check if we have enough moves to do so
            // this is the function of the Math.min
            indexes = getDistinctRandomIntegers(0,movesNoStab.size()-1,
                    Math.min(4- selectedMoves.size(),movesNoStab.size()));
            for (Integer i : indexes){
                selectedMoves.add(movesNoStab.get(i));
            }
        }

        // creates a list with all the remaining stab moves and if necessary completes the list
        // of moves with them
        List<Move> remainingStabMoves = new ArrayList<>();
        remainingStabMoves.addAll(movesStabType1);
        remainingStabMoves.addAll(movesStabType2);

        if (!remainingStabMoves.isEmpty() && (4- selectedMoves.size() > 0)){
            indexes = getDistinctRandomIntegers(0,remainingStabMoves.size()-1,
                    Math.min(4- selectedMoves.size(),remainingStabMoves.size()));
            for (Integer i : indexes){
                selectedMoves.add(remainingStabMoves.get(i));
            }
        }

        return selectedMoves;
    }

    @Override
    protected void onPostExecute(List<Move> moves) {
        super.onPostExecute(moves);
        onResultListener.onResult(moves);
    }

}
