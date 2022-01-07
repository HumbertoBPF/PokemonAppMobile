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
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private Context context;
    private List<Object> pokemons;
    private OnClickListener onClickListener;
    private PokemonTypeDAO pokemonTypeDAO;

    public PokemonAdapter(Context context, List<Object> pokemons, OnClickListener onClickListener){
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
        holder.bind((Pokemon) pokemons.get(position));
    }

    @Override
    public int getItemCount() {
        return pokemons.size();
    }

    class PokemonViewHolder extends RecyclerView.ViewHolder{

        private View itemView;
        private TextView pokemonName;
        private CardView pokemonTypeContainer1;
        private TextView pokemonType1;
        private CardView pokemonTypeContainer2;
        private TextView pokemonType2;
        private TextView pokemonAttack;
        private TextView pokemonDefense;
        private TextView pokemonSpAttack;
        private TextView pokemonSpDefense;
        private TextView pokemonSpeed;
        private TextView pokemonHp;
        private TextView pokemonOverallPoints;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.pokemonName = itemView.findViewById(R.id.pokemon_name);
            this.pokemonTypeContainer1 = itemView.findViewById(R.id.pokemon_type_container_1);
            this.pokemonType1 = itemView.findViewById(R.id.pokemon_type_1);
            this.pokemonTypeContainer2 = itemView.findViewById(R.id.pokemon_type_container_2);
            this.pokemonType2 = itemView.findViewById(R.id.pokemon_type_2);
            this.pokemonAttack = itemView.findViewById(R.id.pokemon_attack);
            this.pokemonDefense = itemView.findViewById(R.id.pokemon_defense);
            this.pokemonSpAttack = itemView.findViewById(R.id.pokemon_sp_attack);
            this.pokemonSpDefense = itemView.findViewById(R.id.pokemon_sp_defense);
            this.pokemonSpeed = itemView.findViewById(R.id.pokemon_speed);
            this.pokemonHp = itemView.findViewById(R.id.pokemon_hp);
            this.pokemonOverallPoints = itemView.findViewById(R.id.pokemon_overall_pts);
        }

        public void bind(Pokemon pokemon){
            this.itemView.setBackground(makeSelector(context.getResources().getColor(R.color.white),0.8f));
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
                    Type type2 = (Type) objects.get(0);
                    pokemonType2.setText(type2.getFName());
                    pokemonTypeContainer2.setCardBackgroundColor(Color.parseColor("#"+ type2.getFColorCode()));
                    if (objects.size() > 1){    // if the pokémon has a second type, add it to layout
                        Type type1 = (Type) objects.get(1);
                        pokemonType1.setVisibility(View.VISIBLE);
                        pokemonType1.setText(type1.getFName());
                        pokemonTypeContainer1.setCardBackgroundColor(Color.parseColor("#"+ type1.getFColorCode()));
                    }else{
                        pokemonType1.setVisibility(View.GONE);
                    }
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
            this.pokemonOverallPoints.setText(context.getResources().getString(R.string.overall_points_label)+
                    " : "+pokemon.getFOverallPts().toString());
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(view, pokemon);
                }
            });
        }

    }

    public interface OnClickListener{
        void onClick(View view, Pokemon pokemon);
    }

}
