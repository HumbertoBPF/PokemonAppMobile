package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.Tools.dualButtonDialog;
import static com.example.pokemonapp.util.Tools.getInGamePokemonFromTeam;
import static com.example.pokemonapp.util.Tools.getOverallPointsOfTeam;
import static com.example.pokemonapp.util.Tools.loadingDialog;
import static com.example.pokemonapp.util.Tools.makeSelector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.activities.game.team.SaveTeamActivity;
import com.example.pokemonapp.async_task.BaseAsyncTask;
import com.example.pokemonapp.dao.TeamDAO;
import com.example.pokemonapp.entities.Team;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class TeamAdapter extends RecyclerView.Adapter<TeamAdapter.TeamViewHolder> {

    private Context context;
    private List<Object> teams;
    private OnItemAdapterClickListener onItemAdapterClickListener;
    private boolean isEditable; // determines if the long click should show the menu with the delete and edit options
    private TeamDAO teamDAO;

    public TeamAdapter(Context context, List<Object> teams, OnItemAdapterClickListener onItemAdapterClickListener, boolean isEditable) {
        this.context = context;
        this.teams = teams;
        this.onItemAdapterClickListener = onItemAdapterClickListener;
        this.isEditable = isEditable;
        this.teamDAO = PokemonAppDatabase.getInstance(this.context).getTeamDAO();
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

    class TeamViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

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
            if (isEditable){
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        public void bind(Team team){
            this.itemView.setBackground(makeSelector(context.getResources().getColor(R.color.white),0.8f));
            this.teamName.setText(team.getName());

            List<InGamePokemon> inGamePokemonList = getInGamePokemonFromTeam(team);

            for (int i=0;i<inGamePokemonList.size();i++){
                pokemonTextView.get(i).setText(inGamePokemonList.get(i).getPokemonServer().getFName());
            }

            this.teamOverallPoints.setText(context.getString(R.string.team_overall_points_label)+" : "+getOverallPointsOfTeam(team));

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemAdapterClickListener.onClick(v, team);
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // creates a context menu with a delete and a edit options triggered by a long click
            MenuItem delete = menu.add(R.string.team_delete_option);
            MenuItem edit = menu.add(R.string.team_edit_option);
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // shows a dialog to confirm that the user wants to delete a team (avoids accidents)
                    AlertDialog confirmDeletionDialog = dualButtonDialog(context, context.getString(R.string.delete_team_dialog_title),
                            context.getString(R.string.delete_team_dialog_text),
                            context.getString(R.string.yes), context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ProgressDialog loadingDialog = loadingDialog(context);
                                    loadingDialog.show();
                                    new BaseAsyncTask(new BaseAsyncTask.BaseAsyncTaskInterface() {
                                        @Override
                                        public List<Object> doInBackground() {
                                            // deletes the clicked element from the database
                                            teamDAO.delete((Team) teams.get(TeamViewHolder.this.getBindingAdapterPosition()));
                                            return null;
                                        }

                                        @Override
                                        public void onPostExecute(List<Object> objects) {
                                            // removes the clicked element from the recycler view list
                                            teams.remove(TeamViewHolder.this.getBindingAdapterPosition());
                                            // communicates the adapter about the change of its list
                                            notifyItemRemoved(TeamViewHolder.this.getBindingAdapterPosition());
                                            loadingDialog.dismiss();
                                            Toast.makeText(context, R.string.team_deleted_confirmation, Toast.LENGTH_LONG).show();
                                        }
                                    }).execute();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    confirmDeletionDialog.show();
                    return false;
                }
            });
            edit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(context, SaveTeamActivity.class);
                    intent.putExtra(context.getString(R.string.key_extra_db_resource),
                            (Team) teams.get(TeamViewHolder.this.getBindingAdapterPosition()));
                    context.startActivity(intent);
                    return false;
                }
            });
        }
    }

}
