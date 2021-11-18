package com.example.pokemonapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonDAO;
import com.example.pokemonapp.dao.PokemonMoveDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.dao.TypeDAO;
import com.example.pokemonapp.dao.TypeEffectiveDAO;
import com.example.pokemonapp.dao.TypeNoEffectDAO;
import com.example.pokemonapp.dao.TypeNotEffectiveDAO;
import com.example.pokemonapp.models.Move;
import com.example.pokemonapp.models.MoveType;
import com.example.pokemonapp.models.Pokemon;
import com.example.pokemonapp.models.PokemonMove;
import com.example.pokemonapp.models.PokemonType;
import com.example.pokemonapp.models.Type;
import com.example.pokemonapp.models.TypeEffective;
import com.example.pokemonapp.models.TypeNoEffect;
import com.example.pokemonapp.models.TypeNotEffective;

@Database(entities = {Pokemon.class, Move.class, Type.class, MoveType.class, PokemonType.class, TypeEffective.class, TypeNotEffective.class, TypeNoEffect.class, PokemonMove.class},
        version = 1,exportSchema = false)
public abstract class PokemonAppDatabase extends RoomDatabase {

    private static final String NAME_DB = "PokemonApp.db";

    public abstract PokemonDAO getPokemonDAO();
    public abstract MoveDAO getMoveDAO();
    public abstract TypeDAO getTypeDAO();
    public abstract MoveTypeDAO getMoveTypeDAO();
    public abstract PokemonTypeDAO getPokemonTypeDAO();
    public abstract PokemonMoveDAO getPokemonMoveDAO();
    public abstract TypeEffectiveDAO getTypeEffectiveDAO();
    public abstract TypeNotEffectiveDAO getTypeNotEffectiveDAO();
    public abstract TypeNoEffectDAO getTypeNoEffectDAO();

    public static PokemonAppDatabase getInstance(Context context){
        return Room
                .databaseBuilder(context,PokemonAppDatabase.class,NAME_DB)
                .build();
    }

}
