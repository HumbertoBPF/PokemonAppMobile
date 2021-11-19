package com.example.pokemonapp.models;

import android.content.Context;
import android.view.View;

import com.example.pokemonapp.R;

public class RoundedButton {

    private String text;
    private int color;
    private View.OnClickListener onClickListener = null;

    public RoundedButton(Context context, String text) {
        this.text = text;
        this.color = context.getResources().getColor(R.color.red);
    }

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
