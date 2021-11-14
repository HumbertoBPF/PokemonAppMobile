package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.Tools.listOfTypesAsString;

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
import com.example.pokemonapp.models.Pokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private Context context;
    private List<Pokemon> pokemons;
    private OnClickListener onClickListener;
    private PokemonTypeDAO pokemonTypeDAO;

    public PokemonAdapter(Context context, List<Pokemon> pokemons, OnClickListener onClickListener){
        this.context = context;
        this.pokemons = pokemons;
        this.onClickListener = onClickListener;
        this.pokemonTypeDAO = PokemonAppDatabase.getInstance(this.context).getPokemonTypeDAO();
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

        private View pokemonView;
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
            this.pokemonView = itemView;
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
            new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                @Override
                public List<Object> doInBackground() {
                    List<Object> objects = new ArrayList<>();
                    objects.addAll(pokemonTypeDAO.getTypesOfPokemon(pokemon.getFId()));
                    return objects;
                }

                @Override
                public void onPostExecute(List<Object> objects) {
                    pokemonTypes.setText(listOfTypesAsString(objects));
                }
            }).execute();
            this.pokemonAttack.setText(context.getResources().getString(R.string.attack_pokemon_label)+
                    "\n"+pokemon.getFAttack().toString());
            this.pokemonDefense.setText(context.getResources().getString(R.string.defense_pokemon_label)+
                    "\n"+pokemon.getFDefense().toString());
            this.pokemonSpAttack.setText(context.getResources().getString(R.string.sp_attack_pokemon_label)+
                    "\n"+pokemon.getFSpAttack().toString());
            this.pokemonSpDefense.setText(context.getResources().getString(R.string.sp_defense_pokemon_label)+
                    "\n"+pokemon.getFSpDefense().toString());
            this.pokemonSpeed.setText(context.getResources().getString(R.string.speed_pokemon_label)+
                    "\n"+pokemon.getFSpeed().toString());
            this.pokemonHp.setText(context.getResources().getString(R.string.hp_pokemon_label)+
                    "\n"+pokemon.getFHp().toString());
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(pokemon);
                }
            });
        }

    }

    public interface OnClickListener{
        void onClick(Pokemon pokemon);
    }

}
