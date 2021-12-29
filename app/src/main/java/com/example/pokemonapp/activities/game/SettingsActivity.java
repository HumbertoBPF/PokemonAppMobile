package com.example.pokemonapp.activities.game;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pokemonapp.R;

public class SettingsActivity extends AppCompatActivity {

    private RadioButton easyOption;
    private RadioButton intermediateOption;
    private RadioButton hardOption;
    private Button saveSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getLayoutElements();

        saveSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (easyOption.isChecked()){
                    saveDifficultLevel("Easy");
                }else if (intermediateOption.isChecked()){
                    saveDifficultLevel("Intermediate");
                }else if (hardOption.isChecked()){
                    saveDifficultLevel("Hard");
                }
                finish();
            }
        });

    }

    private void saveDifficultLevel(String difficultLevel) {
        SharedPreferences sharedPreferences = getSharedPreferences(
                getResources().getString(R.string.name_shared_preferences_file), MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("DifficultLevel", difficultLevel);

        editor.apply();
    }

    private void getLayoutElements() {
        easyOption = findViewById(R.id.easy_option);
        intermediateOption = findViewById(R.id.intermediate_option);
        hardOption = findViewById(R.id.hard_option);
        saveSettingsButton = findViewById(R.id.save_settings_button);
    }

}