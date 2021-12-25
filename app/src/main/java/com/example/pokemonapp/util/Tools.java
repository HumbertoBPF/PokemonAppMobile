package com.example.pokemonapp.util;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemonapp.R;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.models.InGamePokemon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tools {

    public static String listOfTypesAsString(List<Object> objects) {
        String stringTypes = "";
        for (int i = 0;i<objects.size();i++){
            Type type = (Type) objects.get(i);
            stringTypes += type.getFName();
            if (i<objects.size()-1){
                stringTypes += ", ";
            }
        }
        return stringTypes;
    }

    public static String listOfTypesAsStringFromTypeList(List<Type> types) {
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

    public static String listOfMovesAsString(List<Move> moves) {
        String stringMoves = "";
        for (Move move : moves) {
            stringMoves += move.getFName() + ", ";
        }
        return stringMoves;
    }

    public static void setAppbarColor(AppCompatActivity activity, int color){
        ColorDrawable colorDrawable = new ColorDrawable(color);
        activity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    public static AlertDialog yesOrNoDialog(Context context, String title, String message, String yesOption, String noOption,
                                            DialogInterface.OnClickListener onClickListenerYes, DialogInterface.OnClickListener onClickListenerNo){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(yesOption, onClickListenerYes)
                .setNegativeButton(noOption, onClickListenerNo);
        return builder.create();
    }

    public static AlertDialog singleButtonDialog(Context context, String title, String message, String yesOption,
                                                 DialogInterface.OnClickListener onClickListenerYes){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(yesOption, onClickListenerYes);
        return builder.create();
    }

    public static ProgressDialog loadingDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please, wait.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static List<Integer> getDistinctRandomIntegers(int min,int max,int n){
        List<Integer> numbers = new ArrayList<>();
        for (int i = min;i<=max;i++){
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        return numbers.subList(0,n);
    }

    public static List<InGamePokemon> loadTeam(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sharedPreferences.getString(key, null);
        java.lang.reflect.Type type = new TypeToken<ArrayList<InGamePokemon>>() {}.getType();

        return gson.fromJson(json, type);
    }

    public static void saveTeam(Context context, String key, List<InGamePokemon> team) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                context.getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(team);
        editor.putString(key, json);

        editor.apply();
    }

    public static void dismissDialogWhenViewIsDrawn(ViewGroup viewGroup, ProgressDialog dialog) {
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dialog.dismiss();
                viewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public static void goToNextActivityWithStringExtra(Activity activity, String key, String gameMode, Class nextActivity){
        SharedPreferences sharedPreferences = activity.getApplicationContext().getSharedPreferences(
                activity.getApplicationContext().getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString(key, gameMode);
        myEdit.apply();

        activity.startActivity(new Intent(activity.getApplicationContext(), nextActivity));
    }

}
