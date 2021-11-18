package com.example.pokemonapp.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemonapp.models.Move;
import com.example.pokemonapp.models.Type;

import java.util.List;

public class Tools {

    public static String listOfTypesAsString(List<Object> objects) {
        String stringTypes = "";
        for (int i = 0;i<objects.size();i++){
            Type type = (Type) objects.get(i);
            stringTypes += type.getFName();
            if (i<objects.size()-1){
                stringTypes += ",";
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
                stringTypes += ",";
            }
        }
        return stringTypes;
    }

    public static String listOfMovesAsString(List<Move> moves) {
        String stringMoves = "";
        for (Move move : moves) {
            stringMoves += move.getFName() + ",";
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

    public static ProgressDialog loadingDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please, wait.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

}
