package com.example.pokemonapp.async_task;

import android.content.Context;
import android.os.AsyncTask;

import com.example.pokemonapp.dao.MoveDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

public class StruggleMoveTask extends AsyncTask<Void,Void, Move> {

    private Context context;
    private OnMoveSelectionListener onMoveSelectionListener;

    public StruggleMoveTask(Context context, OnMoveSelectionListener onMoveSelectionListener) {
        this.context = context;
        this.onMoveSelectionListener = onMoveSelectionListener;
    }

    @Override
    protected Move doInBackground(Void... voids) {
        MoveDAO moveDAO = PokemonAppDatabase.getInstance(context).getMoveDAO();
        return moveDAO.getMoveByName("Struggle");
    }

    @Override
    protected void onPostExecute(Move move) {
        super.onPostExecute(move);
        onMoveSelectionListener.onMoveSelection(move);
    }

    public interface OnMoveSelectionListener{
        void onMoveSelection(Move move);
    }

}
