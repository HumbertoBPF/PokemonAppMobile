package com.example.pokemonapp.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

import androidx.appcompat.app.AppCompatActivity;

public class UiTools {

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
