package com.example.pokemonapp.util;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.pokemonapp.R;
import com.example.pokemonapp.models.InGamePokemon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesTools {

    /**
     * Loads a team(List of InGamePokemon objects) from Shared Preferences.
     * @param context context of the activity calling the method.
     * @param key key of the wanted attribute in Shared Preferences.
     * @return a List of InGamePokemmon objects corresponding to a team.
     */
    public static List<InGamePokemon> loadTeam(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        java.lang.reflect.Type type = new TypeToken<ArrayList<InGamePokemon>>() {}.getType();

        return gson.fromJson(json, type);
    }

    /**
     * Saves a team(list of InGamePokemon) in Shared Preferences.
     * @param context context of the activity calling the method.
     * @param key key of the attribute in Shared Preferences.
     * @param team list of InGamePokemon to be saved in Shared Preferences.
     */
    public static void saveTeam(Context context, String key, List<InGamePokemon> team) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(team);
        editor.putString(key, json);

        editor.apply();
    }

    /**
     * Goes to the specified activity with a String extra.
     * @param activity current activity.
     * @param key key of the extra.
     * @param stringExtra String extra to be carried.
     * @param nextActivity next activity.
     */
    public static void goToNextActivityWithStringExtra(Activity activity, String key, String stringExtra, Class nextActivity){
        SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences(
                activity.getApplicationContext().getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(key, stringExtra);
        myEdit.apply();

        activity.startActivity(new Intent(activity.getApplicationContext(), nextActivity));
    }

    /**
     * Gets a list of InGamePokemon object from a JSON String.
     * @param JSONString JSON String corresponding to a InGamePokemon.
     * @return the correspondent InGamePokemon object.
     */
    @Nullable
    public static List<InGamePokemon> getInGamePokemonFromJSON(String JSONString) {
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<ArrayList<InGamePokemon>>() {}.getType();
        return gson.fromJson(JSONString, type);
    }

}
