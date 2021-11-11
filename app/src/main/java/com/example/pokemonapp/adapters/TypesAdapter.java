package com.example.pokemonapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.models.Type;

import java.util.List;

public class TypesAdapter extends RecyclerView.Adapter<TypesAdapter.TypeViewHolder> {

    private Context context;
    private List<Type> types;

    public TypesAdapter(Context context, List<Type> types){
        this.context = context;
        this.types = types;
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
            this.nbPokemonType.setText("Number of pokémon with this type");
        }

    }

}
