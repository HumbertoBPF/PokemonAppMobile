package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.Tools.listOfTypesAsString;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.dao.PokemonTypeDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Pokemon;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class PokemonMovesAdapter extends RecyclerView.Adapter<PokemonMovesAdapter.PokemonMovesViewHolder> {

    private Context context;
    private List<InGamePokemon> inGamePokemonList;
    private PokemonTypeDAO pokemonTypeDAO;
    private MoveTypeDAO moveTypeDAO;

    public PokemonMovesAdapter(Context context, List<InGamePokemon> inGamePokemonList){
        this.context = context;
        this.inGamePokemonList = inGamePokemonList;
        this.pokemonTypeDAO = PokemonAppDatabase.getInstance(this.context).getPokemonTypeDAO();
        this.moveTypeDAO = PokemonAppDatabase.getInstance(this.context).getMoveTypeDAO();
    }

    @NonNull
    @Override
    public PokemonMovesAdapter.PokemonMovesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.pokemon_moves_adapter_layout,parent,false);
        return new PokemonMovesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonMovesAdapter.PokemonMovesViewHolder holder, int position) {
        holder.bind(inGamePokemonList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.inGamePokemonList.size();
    }

    class PokemonMovesViewHolder extends RecyclerView.ViewHolder{

        private View pokemonView;
        private TextView pokemonName;
        private TextView pokemonTypes;
        private TextView pokemonAttack;
        private TextView pokemonDefense;
        private TextView pokemonSpAttack;
        private TextView pokemonSpDefense;
        private TextView pokemonSpeed;
        private TextView pokemonHp;

        private List<TextView> moveNames = new ArrayList<>();
        private List<TextView> moveTypes = new ArrayList<>();
        private List<TextView> moveCategories = new ArrayList<>();
        private List<TextView> movePowers = new ArrayList<>();
        private List<TextView> moveAccuracies = new ArrayList<>();
        private List<TextView> movePps = new ArrayList<>();

        public PokemonMovesViewHolder(@NonNull View itemView) {
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

            this.moveNames.add(itemView.findViewById(R.id.move1_name));
            this.moveTypes.add(itemView.findViewById(R.id.move1_type));
            this.moveCategories.add(itemView.findViewById(R.id.move1_category));
            this.movePowers.add(itemView.findViewById(R.id.move1_power));
            this.moveAccuracies.add(itemView.findViewById(R.id.move1_accuracy));
            this.movePps.add(itemView.findViewById(R.id.move1_pp));

            this.moveNames.add(itemView.findViewById(R.id.move2_name));
            this.moveTypes.add(itemView.findViewById(R.id.move2_type));
            this.moveCategories.add(itemView.findViewById(R.id.move2_category));
            this.movePowers.add(itemView.findViewById(R.id.move2_power));
            this.moveAccuracies.add(itemView.findViewById(R.id.move2_accuracy));
            this.movePps.add(itemView.findViewById(R.id.move2_pp));

            this.moveNames.add(itemView.findViewById(R.id.move3_name));
            this.moveTypes.add(itemView.findViewById(R.id.move3_type));
            this.moveCategories.add(itemView.findViewById(R.id.move3_category));
            this.movePowers.add(itemView.findViewById(R.id.move3_power));
            this.moveAccuracies.add(itemView.findViewById(R.id.move3_accuracy));
            this.movePps.add(itemView.findViewById(R.id.move3_pp));

            this.moveNames.add(itemView.findViewById(R.id.move4_name));
            this.moveTypes.add(itemView.findViewById(R.id.move4_type));
            this.moveCategories.add(itemView.findViewById(R.id.move4_category));
            this.movePowers.add(itemView.findViewById(R.id.move4_power));
            this.moveAccuracies.add(itemView.findViewById(R.id.move4_accuracy));
            this.movePps.add(itemView.findViewById(R.id.move4_pp));

        }

        public void bind(InGamePokemon inGamePokemon){
            Pokemon pokemon = inGamePokemon.getPokemonServer();
            List<Move> moves = inGamePokemon.getMoves();
            Log.i("movesList",moves.size()+"");
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
            for (int i=0;i<4;i++){
                if (i > moves.size()-1){
                    this.moveNames.get(i).setVisibility(View.INVISIBLE);
                    this.moveTypes.get(i).setVisibility(View.INVISIBLE);
                    this.moveCategories.get(i).setVisibility(View.INVISIBLE);
                    this.movePowers.get(i).setVisibility(View.INVISIBLE);
                    this.moveAccuracies.get(i).setVisibility(View.INVISIBLE);
                    this.movePps.get(i).setVisibility(View.INVISIBLE);
                }else{
                    Move move = moves.get(i);
                    this.moveNames.get(i).setText(move.getFName());
                    TextView moveType = moveTypes.get(i);
                    new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                        @Override
                        public List<Object> doInBackground() {
                            List<Object> objects = new ArrayList<>();
                            objects.addAll(moveTypeDAO.getTypesOfMove(move.getFId()));
                            return objects;
                        }

                        @Override
                        public void onPostExecute(List<Object> objects) {
                            moveType.setText(listOfTypesAsString(objects));
                        }
                    }).execute();
                    this.moveCategories.get(i).setText(move.getFCategory());
                    this.movePowers.get(i).setText(context.getResources().getString(R.string.power_move_label)+" : "+move.getFPower().toString());
                    this.moveAccuracies.get(i).setText(context.getResources().getString(R.string.accuracy_move_label)+" : "+move.getFAccuracy().toString());
                    this.movePps.get(i).setText(context.getResources().getString(R.string.pp_move_label)+" : "+move.getFPp().toString());
                }
            }
        }

    }

}
