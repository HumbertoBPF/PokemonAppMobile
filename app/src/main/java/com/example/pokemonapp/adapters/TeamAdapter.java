package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.Tools.getInGamePokemonFromJSON;
import static com.example.pokemonapp.util.Tools.getOverallPointsOfTeam;
import static com.example.pokemonapp.util.Tools.makeSelector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.models.InGamePokemon;

import java.util.ArrayList;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private Context context;
    private List<Object> teams;
    private OnClickListener onClickListener;

    public TeamAdapter(Context context, List<Object> teams, OnClickListener onClickListener) {
        this.context = context;
        this.teams = teams;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public TeamAdapter.TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View typeItem = LayoutInflater.from(context)
                .inflate(R.layout.team_adapter_layout,parent,false);
        return new TeamAdapter.TeamViewHolder(typeItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamAdapter.TeamViewHolder holder, int position) {
        holder.bind((Team) teams.get(position));
    }

    @Override
    public int getItemCount() {
        return teams.size();
    }

    class TeamViewHolder extends RecyclerView.ViewHolder{

        private TextView teamName;
        private List<TextView> pokemonTextView = new ArrayList<>();
        private TextView teamOverallPoints;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            teamName = itemView.findViewById(R.id.team_name);
            pokemonTextView.add(itemView.findViewById(R.id.pokemon_1));
            pokemonTextView.add(itemView.findViewById(R.id.pokemon_2));
            pokemonTextView.add(itemView.findViewById(R.id.pokemon_3));
            pokemonTextView.add(itemView.findViewById(R.id.pokemon_4));
            pokemonTextView.add(itemView.findViewById(R.id.pokemon_5));
            pokemonTextView.add(itemView.findViewById(R.id.pokemon_6));
            teamOverallPoints = itemView.findViewById(R.id.team_overall_points);
        }

        public void bind(Team team){
            this.itemView.setBackground(makeSelector(context.getResources().getColor(R.color.white),0.8f));
            this.teamName.setText(team.getName());

            List<InGamePokemon> inGamePokemonList = getInGamePokemonFromJSON(team);

            for (int i=0;i<inGamePokemonList.size();i++){
                pokemonTextView.get(i).setText(inGamePokemonList.get(i).getPokemonServer().getFName());
            }

            this.teamOverallPoints.setText(context.getString(R.string.team_overall_points_label)+" : "+getOverallPointsOfTeam(team));

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(team);
                }
            });
        }

    }

    public interface OnClickListener{
        void onClick(Team team);
    }

}
