package com.example.pokemonapp.dao;

import android.os.AsyncTask;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.pokemonapp.async_task.OnResultListener;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.MoveType;
import com.example.pokemonapp.entities.Type;

import java.util.List;

@Dao
public abstract class MoveTypeDAO extends RemoteDAO<MoveType> {

    public MoveTypeDAO() {
        super("move_type");
    }

    @Query("SELECT * FROM type INNER JOIN move_type ON type.fId = move_type.typeId WHERE move_type.moveId = :moveId")
    public abstract List<Type> getTypesOfMove(long moveId);

    @Query("SELECT COUNT(*) FROM move_type;")
    public abstract Long getNbOfElements();

    @Query("SELECT COUNT(*) FROM move_type WHERE move_type.typeId = :typeId")
    public abstract Integer getNbMovesWithThisType(long typeId);

    public AsyncTask<Void,Void,List<Type>> TypesOfMoveTask(Move move, OnResultListener<List<Type>> onResultListener){
        return new AsyncTask<Void, Void, List<Type>>() {
            @Override
            protected List<Type> doInBackground(Void... voids) {
                return getTypesOfMove(move.getFId());
            }

            @Override
            protected void onPostExecute(List<Type> types) {
                super.onPostExecute(types);
                onResultListener.onResult(types);
            }
        };
    }

    public AsyncTask<Void,Void,Integer> nbMovesWithThisTypeTask(Type type, OnResultListener<Integer> onResultListener){
        return new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                return getNbMovesWithThisType(type.getFId());
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                onResultListener.onResult(integer);
            }
        };
    }

}
