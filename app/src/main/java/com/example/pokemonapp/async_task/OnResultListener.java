package com.example.pokemonapp.async_task;

/**
 * Interface to execute some code after an entity is provided as result of a previous task.
 * @param <E> generic specifying the object to be handled.
 */
public interface OnResultListener<E> {
    void onResult(E result);
}
