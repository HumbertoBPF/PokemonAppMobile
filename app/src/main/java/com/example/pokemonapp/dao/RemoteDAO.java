package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Insert;

import java.util.List;

/**
 * DAO specifying the operations that are common to all the entities also available on remote.
 * @param <E> entity concerned.
 */
public abstract class RemoteDAO<E> extends BaseDAO<E>{

    public RemoteDAO(String tableName) {
        super(tableName);
    }

    /**
     * Saves locally all the entities contained in the list specified as parameter.
     * @param entities list of entities to be saved.
     */
    @Insert(onConflict = REPLACE)
    public abstract void save(List<E> entities);

}
