package com.example.pokemonapp.async_task;

import com.example.pokemonapp.dao.LocalDAO;

public class SaveLocalResourceTask<E> extends SaveResourceTask<E> {

    public SaveLocalResourceTask(LocalDAO<E> localDAO, E entities, OnSavingListener onSavingListener) {
        super(localDAO, entities, onSavingListener);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ((LocalDAO<E>) baseDAO).save((E) entities);
        return null;
    }
}
