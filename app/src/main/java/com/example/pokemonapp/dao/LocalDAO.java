package com.example.pokemonapp.dao;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Delete;
import androidx.room.Insert;

/**
 * DAO specifying the operations that are common to all the entities available only on local.
 * @param <E> entity concerned.
 */
public abstract class LocalDAO<E> extends BaseDAO<E> {

    public LocalDAO(String tableName) {
        super(tableName);
    }

    /**
     * Saves locally the entity specified as argument.
     * @param entity entity to be saved.
     */
    @Insert(onConflict = REPLACE)
    public abstract void save(E entity);

    /**
     * Deletes locally the entity specified as argument.
     * @param entity entity to be deleted.
     */
    @Delete
    public abstract void delete(E entity);

}
