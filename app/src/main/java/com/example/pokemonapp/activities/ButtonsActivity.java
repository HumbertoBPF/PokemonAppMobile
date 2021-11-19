package com.example.pokemonapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.RoundedButtonListAdapter;
import com.example.pokemonapp.models.RoundedButton;

import java.util.ArrayList;
import java.util.List;

public abstract class ButtonsActivity extends AppCompatActivity {

    private RecyclerView buttonsRecyclerView;
    protected List<RoundedButton> buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        declareButtons();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons);
        buttonsRecyclerView = findViewById(R.id.buttons_recycler_view);
        buttonsRecyclerView.setAdapter(new RoundedButtonListAdapter(this,buttons));
    }

    protected abstract void declareButtons();

}