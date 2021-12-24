package com.example.pokemonapp.models;

import android.view.View;

/**
 * Object to configure each button to be shown on the screen. Each button has :<br>
 * <br>
 *     - <b>text</b><br>
 *     - <b>color</b><br>
 *     - <b>onClickListener</b><br>
 */
public class RoundedButton {

    private String text;
    private int color;
    private View.OnClickListener onClickListener;

    public RoundedButton(String text, int color, View.OnClickListener onClickListener) {
        this.text = text;
        this.color = color;
        this.onClickListener = onClickListener;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
