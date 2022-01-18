package com.example.pokemonapp.async_task;

import com.example.pokemonapp.dao.RemoteDAO;

import java.util.List;

public class SaveRemoteResourcesTask<E> extends SaveResourceTask<E> {

    public SaveRemoteResourcesTask(RemoteDAO<E> remoteDAO, List<E> entities, OnSavingListener onSavingListener) {
        super(remoteDAO, entities, onSavingListener);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ((RemoteDAO<E>) baseDAO).save((List<E>) entities);
        return null;
    }
}
