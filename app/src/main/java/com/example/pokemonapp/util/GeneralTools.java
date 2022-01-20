package com.example.pokemonapp.util;

import android.content.Context;

import com.example.pokemonapp.R;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.models.InGamePokemon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneralTools {

    /**
     * Converts a List&lt;Type&gt; into a string with the types of the list separated by commas.
     * @param types list of types.
     * @return a String containing all the types separated by commas.
     */
    public static String listOfTypesAsString(List<Type> types) {
        String stringTypes = "";
        for (int i = 0;i<types.size();i++){
            Type type = types.get(i);
            stringTypes += type.getFName();
            if (i<types.size()-1){
                stringTypes += ", ";
            }
        }
        return stringTypes;
    }

    /**
     * Gets a list with n integers in the range [min,max].
     * @param min left bound of the interval.
     * @param max right bound of the interval.
     * @param n number of integers to be returned inside the interval. It must be greater than the
     *          max-min+1 (number of integers inside the interval).
     * @return a list with n random integers inside the specified interval.
     */
    public static List<Integer> getDistinctRandomIntegers(int min,int max,int n){
        List<Integer> numbers = new ArrayList<>();
        for (int i = min;i<=max;i++){
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers.subList(0,n);
    }

    /**
     * @param inGamePokemonList list of pokémon (InGamePokemon objects).
     * @return sum of the Overall Points of the pokémon of the specified team.
     */
    public static int getOverallPointsOfTeam(List<InGamePokemon> inGamePokemonList) {
        int teamOverallPoints = 0;
        for (InGamePokemon inGamePokemon : inGamePokemonList){
            teamOverallPoints += inGamePokemon.getPokemonServer().getFOverallPts();
        }
        return teamOverallPoints;
    }

    /**
     * Converts the mnemonic representing a game mode into a more readable string.
     * @param context context of the activity calling the method.
     * @param gameModeMnemo mnemonic used to represent a game mode.
     * @return the String more readable representing the game mode.
     */
    public static String getGameModeStringFromMnemonic(Context context, String gameModeMnemo) {
        if (gameModeMnemo.equals(context.getString(R.string.label_favorite_team_mode))) {
            return context.getString(R.string.favorite_team_mode_button_text);
        } else if (gameModeMnemo.equals(context.getString(R.string.label_strategy_mode))) {
            return context.getString(R.string.strategy_mode_button_text);
        } else if (gameModeMnemo.equals(context.getString(R.string.label_random_mode))) {
            return context.getString(R.string.random_mode_button_text);
        }
        return "";
    }

    /**
     * Converts the mnemonic representing a game level into a more readable string.
     * @param context context of the activity calling the method.
     * @param gameLevelMnemo mnemonic used to represent a game level.
     * @return the String more readable representing the game level.
     */
    public static String getGameLevelStringFromMnemonic(Context context, String gameLevelMnemo) {
        if (gameLevelMnemo.equals(context.getString(R.string.easy_level))){
            return context.getString(R.string.easy_level_text);
        }else if (gameLevelMnemo.equals(context.getString(R.string.intermediate_level))){
            return context.getString(R.string.intermediate_level_text);
        }else if (gameLevelMnemo.equals(context.getString(R.string.hard_level))){
            return context.getString(R.string.hard_level_text);
        }
        return "";
    }

}
