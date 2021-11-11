package com.example.pokemonapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.models.Pokemon;

import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private Context context;
    private List<Pokemon> pokemons;

    public PokemonAdapter(Context context, List<Pokemon> pokemons){
        this.context = context;
        this.pokemons = pokemons;
    }

    @NonNull
    @Override
    public PokemonAdapter.PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View pokemonItem = LayoutInflater.from(context)
                .inflate(R.layout.pokemon_adapter_layout,parent,false);
        return new PokemonViewHolder(pokemonItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonAdapter.PokemonViewHolder holder, int position) {
        holder.bind(pokemons.get(position));
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    class PokemonViewHolder extends RecyclerView.ViewHolder{

        private TextView pokemonName;
        private TextView pokemonTypes;
        private TextView pokemonAttack;
        private TextView pokemonDefense;
        private TextView pokemonSpAttack;
        private TextView pokemonSpDefense;
        private TextView pokemonSpeed;
        private TextView pokemonHp;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            this.pokemonName = itemView.findViewById(R.id.pokemon_name);
            this.pokemonTypes = itemView.findViewById(R.id.pokemon_types);
            this.pokemonAttack = itemView.findViewById(R.id.pokemon_attack);
            this.pokemonDefense = itemView.findViewById(R.id.pokemon_defense);
            this.pokemonSpAttack = itemView.findViewById(R.id.pokemon_sp_attack);
            this.pokemonSpDefense = itemView.findViewById(R.id.pokemon_sp_defense);
            this.pokemonSpeed = itemView.findViewById(R.id.pokemon_speed);
            this.pokemonHp = itemView.findViewById(R.id.pokemon_hp);
        }

        public void bind(Pokemon pokemon){
            this.pokemonName.setText(pokemon.getFName());
            this.pokemonTypes.setText("Types");
            this.pokemonAttack.setText("Att.\n"+pokemon.getFAttack().toString());
            this.pokemonDefense.setText("Def.\n"+pokemon.getFDefense().toString());
            this.pokemonSpAttack.setText("Sp.Att.\n"+pokemon.getFSpAttack().toString());
            this.pokemonSpDefense.setText("Sp.Def.\n"+pokemon.getFSpDefense().toString());
            this.pokemonSpeed.setText("Speed\n"+pokemon.getFSpeed().toString());
            this.pokemonHp.setText("Hp\n"+pokemon.getFHp().toString());
        }

    }

}
