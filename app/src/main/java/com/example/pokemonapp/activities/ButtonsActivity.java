package com.example.pokemonapp.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.adapters.RoundedButtonListAdapter;
import com.example.pokemonapp.models.RoundedButton;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>ButtonsActivity</b> : this activity is used for screens where we have only some buttons whose width
 * corresponds to almost the whole width of the screen. The content and specificities of each button is
 * informed using a list of <b>RoundedButton</b> that is loaded into a RecyclerView.
 */
public abstract class ButtonsActivity extends AppCompatActivity {

    private RecyclerView buttonsRecyclerView;
    protected List<RoundedButton> buttons = new ArrayList<>();  // list of buttons to be shown in the activity layout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        declareButtons();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons);
        buttonsRecyclerView = findViewById(R.id.buttons_recycler_view);
        buttonsRecyclerView.setAdapter(new RoundedButtonListAdapter(this,buttons));
    }

    /**
     * This method should be used to initialize the list of buttons, that is to specify the text, color
     * and listener associated to each button to be shown.
     */
    protected abstract void declareButtons();

}