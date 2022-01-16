package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.Tools.makeSelector;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TypesAdapter extends RecyclerView.Adapter<TypesAdapter.TypeViewHolder> {

    private Context context;
    private List<Object> types;
    private PokemonTypeDAO pokemonTypeDAO;
    private OnItemAdapterClickListener onItemAdapterClickListener;

    public TypesAdapter(Context context, List<Object> types, OnItemAdapterClickListener onItemAdapterClickListener){
        this.context = context;
        this.types = types;
        this.pokemonTypeDAO = PokemonAppDatabase.getInstance(this.context).getPokemonTypeDAO();
        this.onItemAdapterClickListener = onItemAdapterClickListener;
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
        holder.bind((Type) types.get(position));
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    class TypeViewHolder extends RecyclerView.ViewHolder{

        private View itemView;
        private CardView typeNameContainer;
        private TextView typeName;
        private TextView nbPokemonType;

        public TypeViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.typeNameContainer = itemView.findViewById(R.id.type_name_container);
            this.typeName = itemView.findViewById(R.id.type_name);
            this.nbPokemonType = itemView.findViewById(R.id.nb_pokemon_type);
        }

        public void bind(Type type){
            this.itemView.setBackground(makeSelector(context.getResources().getColor(R.color.white),0.8f));
            this.typeNameContainer.setCardBackgroundColor(Color.parseColor("#"+type.getFColorCode()));
            this.typeName.setText(type.getFName());
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    List<Object> objects = new ArrayList<>();
                    objects.add(pokemonTypeDAO.getNbPokemonWithThisType(type.getFId()));
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    Integer nbOfPokemon = (Integer) objects.get(0);
                    nbPokemonType.setText(nbOfPokemon+context.getString(R.string.pokemon_have_type));
                }
            }).execute();
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemAdapterClickListener.onClick(view, type);
                }
            });
        }

    }

}
