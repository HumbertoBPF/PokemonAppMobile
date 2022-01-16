package com.example.pokemonapp.dao;

import androidx.room.Dao;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

/**
 * DAO allowing to perform the most basic operations for all entities of the database.
 * @param <E> entity concerned.
 */
@Dao
public abstract class BaseDAO<E> {

    protected String tableName;

    /**
     * @param tableName name of the table corresponding to the entity <b>E</b>.
     */
    public BaseDAO(String tableName) {
        this.tableName = tableName;
    }

    @RawQuery
    protected abstract List<E> getAllRecords(SupportSQLiteQuery sqLiteQuery);

    /**
     * Executes the query to get the list of all the records concerning the entity <b>E</b>.
     * @return
     */
    public List<E> getAllRecords(){
        return getAllRecords(new SimpleSQLiteQuery("SELECT * FROM "+tableName));
    }

}
