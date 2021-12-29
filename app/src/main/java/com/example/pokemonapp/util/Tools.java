package com.example.pokemonapp.util;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Spanned;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemonapp.R;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.models.InGamePokemon;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Tools {

    /**
     * Converts a List&lt;Object&gt; of types into a string with the types of the list separated
     * by commas.
     * @param objects list of objects containing types.
     * @return a String containing all the types separated by commas.
     */
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

    /**
     * Converts a List&lt;Type&gt; into a string with the types of the list separated by commas.
     * @param types list of types.
     * @return a String containing all the types separated by commas.
     */
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

    /**
     * Sets the color of the appbar.
     * @param activity activity calling this method.
     * @param color color to be set.
     */
    public static void setAppbarColor(AppCompatActivity activity, int color){
        ColorDrawable colorDrawable = new ColorDrawable(color);
        activity.getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    /**
     * Creates an AlertDialog object with two buttons.
     * @param context context of the activity calling this method.
     * @param title String defining the title of the dialog.
     * @param message String defining the text in the body of this dialog.
     * @param rightButtonText String defining text of the button on the right.
     * @param leftButtonText String defining text of the button on the left.
     * @param onClickListenerRightButton listener of the button on the right.
     * @param onClickListenerLeftButton listener of the button on the left.
     * @return a dialog object with two buttons.
     */
    public static AlertDialog dualButtonDialog(Context context, String title, String message,
                                               String rightButtonText, String leftButtonText,
                                               DialogInterface.OnClickListener onClickListenerRightButton,
                                               DialogInterface.OnClickListener onClickListenerLeftButton){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(rightButtonText, onClickListenerRightButton)
                .setNegativeButton(leftButtonText, onClickListenerLeftButton);
        return builder.create();
    }

    /**
     * Creates an AlertDialog object with one button.
     * @param context context of the activity calling this method.
     * @param title String defining the title of the dialog.
     * @param message String defining the text in the body of this dialog.
     * @param buttonText String defining the text of the button.
     * @param onClickListenerButton listener associated to the button.
     * @return a dialog object with one button.
     */
    public static AlertDialog singleButtonDialog(Context context, String title, String message, String buttonText,
                                                 DialogInterface.OnClickListener onClickListenerButton){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(buttonText, onClickListenerButton);
        return builder.create();
    }

    /**
     * Creates an AlertDialog object with one button.
     * @param context context of the activity calling this method.
     * @param title String defining the title of the dialog.
     * @param message Spanned object defining the text in the body of this dialog(a Spanned object
     *                allows us to define the text by using HTML and converting it to a Spanned by
     *                using the method Html.fromHtml().
     * @param buttonText String defining the text of the button.
     * @param onClickListenerButton listener associated to the button.
     * @return a dialog object with one button.
     */
    public static AlertDialog singleButtonDialog(Context context, String title, Spanned message, String buttonText,
                                                 DialogInterface.OnClickListener onClickListenerButton){
        AlertDialog.Builder builder =  new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(buttonText, onClickListenerButton);
        return builder.create();
    }

    /**
     * Creates a ProgressDialog object with a spinner indicating that something is being processed.
     * When the processing finished it can be dismissed to allow the user to interact with the UI.
     * @param context context of the activity calling the method.
     * @return a ProgressDialog object with a spinner indicating that something is being loaded.
     */
    public static ProgressDialog loadingDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Processing...");
        progressDialog.setMessage("Please, wait.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);    // it can be set to true if one wants to allow the
                                                // dialog to be dismissed when clicking out of it
        return progressDialog;
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
     * dismisses the specified ProgressDialog when the specified view is drawn.
     * @param viewGroup view that will determine the dismissing of the dialog.
     * @param dialog ProgressDialog to be dismissed.
     */
    public static void dismissDialogWhenViewIsDrawn(ViewGroup viewGroup, ProgressDialog dialog) {
        viewGroup.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                dialog.dismiss();
                viewGroup.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
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
     * Builds a StateListDrawable that can be used to set a hover effect on a view(the view has
     * <b>color</b> when not pressed and a darkened version of this color when pressed).
     * @param color color to be used.
     * @param factorPressedColor factor between 0 and 1 that is used to darken the color so as to
     *                           determine the pressed color(more it is lower, more the color is
     *                           darken).
     * @return a StateListDrawable that can be used to set a hover effect on a view.
     */
    public static StateListDrawable makeSelector(int color, float factorPressedColor) {
        StateListDrawable res = new StateListDrawable();
        GradientDrawable colorPressed = new GradientDrawable();
        colorPressed.setColor(manipulateColor(color,factorPressedColor));
        colorPressed.setCornerRadius(20);
        GradientDrawable normalColor = new GradientDrawable();
        normalColor.setColor(color);
        normalColor.setCornerRadius(20);
        res.addState(new int[]{android.R.attr.state_pressed}, colorPressed);
        res.addState(new int[]{}, normalColor);
        return res;
    }

    /**
     * Darkens the specified color.
     * @param color color to be darken.
     * @param factor factor between 0 and 1 to inform how much the specified color should be darken
     *               (more it is lower, more the color is darken).
     * @return a darkened version of the color.
     */
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a, Math.min(r,255), Math.min(g,255), Math.min(b,255));
    }

}
