package com.example.pokemonapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.models.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TypesAdapter extends RecyclerView.Adapter<TypesAdapter.TypeViewHolder> {

    private Context context;
    private List<Type> types;
    private PokemonTypeDAO pokemonTypeDAO;

    public TypesAdapter(Context context, List<Type> types){
        this.context = context;
        this.types = types;
        this.pokemonTypeDAO = PokemonAppDatabase.getInstance(this.context).getPokemonTypeDAO();
    }

    @NonNull
    @Override
    public TypesAdapter.TypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View typeItem = LayoutInflater.from(context)
                .inflate(R.layout.types_adapter_layout,parent,false);
        return new TypeViewHolder(typeItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TypesAdapter.TypeViewHolder holder, int position) {
        holder.bind(types.get(position));
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    class TypeViewHolder extends RecyclerView.ViewHolder{

        private TextView typeName;
        private TextView nbPokemonType;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.typeName = itemView.findViewById(R.id.type_name);
            this.nbPokemonType = itemView.findViewById(R.id.nb_pokemon_type);
        }

        public void bind(Type type){
            this.typeName.setText(type.getFName());
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    List<Object> objects = new ArrayList<>();
                    objects.add(pokemonTypeDAO.getPokemonWithThisType(type.getFId()));
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    Integer nbOfPokemon = (Integer) objects.get(0);
                    nbPokemonType.setText(nbOfPokemon+" pokemon with this type");
                }
            }).execute();
        }

    }

}
