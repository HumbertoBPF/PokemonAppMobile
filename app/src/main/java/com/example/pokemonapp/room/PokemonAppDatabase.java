package com.example.pokemonapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.pokemonapp.dao.local.GameDAO;
import com.example.pokemonapp.dao.local.GameTeamDAO;
import com.example.pokemonapp.dao.local.InGamePokemonDAO;
import com.example.pokemonapp.dao.local.InGamePokemonMoveDAO;
import com.example.pokemonapp.dao.local.InGamePokemonTeamDAO;
import com.example.pokemonapp.dao.local.TeamDAO;
import com.example.pokemonapp.dao.server_side.MoveDAO;
import com.example.pokemonapp.dao.server_side.MoveTypeDAO;
import com.example.pokemonapp.dao.server_side.PokemonDAO;
import com.example.pokemonapp.dao.server_side.PokemonMoveDAO;
import com.example.pokemonapp.dao.server_side.PokemonTypeDAO;
import com.example.pokemonapp.dao.server_side.TypeDAO;
import com.example.pokemonapp.dao.server_side.TypeEffectiveDAO;
import com.example.pokemonapp.dao.server_side.TypeNoEffectDAO;
import com.example.pokemonapp.dao.server_side.TypeNotEffectiveDAO;
import com.example.pokemonapp.entities.local.Game;
import com.example.pokemonapp.entities.local.GameTeam;
import com.example.pokemonapp.entities.local.InGamePokemon;
import com.example.pokemonapp.entities.local.InGamePokemonMove;
import com.example.pokemonapp.entities.local.InGamePokemonTeam;
import com.example.pokemonapp.entities.local.Team;
import com.example.pokemonapp.entities.server_side.Move;
import com.example.pokemonapp.entities.server_side.MoveType;
import com.example.pokemonapp.entities.server_side.Pokemon;
import com.example.pokemonapp.entities.server_side.PokemonMove;
import com.example.pokemonapp.entities.server_side.PokemonType;
import com.example.pokemonapp.entities.server_side.Type;
import com.example.pokemonapp.entities.server_side.TypeEffective;
import com.example.pokemonapp.entities.server_side.TypeNoEffect;
import com.example.pokemonapp.entities.server_side.TypeNotEffective;

@Database(entities = {Pokemon.class, Move.class, Type.class, MoveType.class, PokemonType.class,
        TypeEffective.class, TypeNotEffective.class, TypeNoEffect.class, PokemonMove.class,
        InGamePokemon.class, Team.class, Game.class, GameTeam.class, InGamePokemonMove.class, InGamePokemonTeam.class},
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
    public abstract InGamePokemonDAO getInGamePokemonDAO();
    public abstract TeamDAO getTeamDAO();
    public abstract GameDAO getGameDAO();
    public abstract GameTeamDAO getGameTeamDAO();
    public abstract InGamePokemonMoveDAO getInGamePokemonMoveDAO();
    public abstract InGamePokemonTeamDAO getInGamePokemonTeamDAO();


    public static PokemonAppDatabase getInstance(Context context){
        return Room
                .databaseBuilder(context,PokemonAppDatabase.class,NAME_DB)
                .build();
    }

}
