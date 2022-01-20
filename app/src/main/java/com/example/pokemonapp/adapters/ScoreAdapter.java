package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.DialogTools.dualButtonDialog;
import static com.example.pokemonapp.util.DialogTools.loadingDialog;
import static com.example.pokemonapp.util.GeneralTools.getGameLevelStringFromMnemonic;
import static com.example.pokemonapp.util.GeneralTools.getGameModeStringFromMnemonic;
import static com.example.pokemonapp.util.SharedPreferencesTools.getInGamePokemonFromJSON;
import static com.example.pokemonapp.util.UiTools.makeSelector;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.pokemonapp.async_task.OnTaskListener;
import com.example.pokemonapp.dao.ScoreDAO;
import com.example.pokemonapp.entities.Score;
import com.example.pokemonapp.models.InGamePokemon;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {

    private Context context;
    private List<Score> scores;
    private OnItemAdapterClickListener onItemAdapterClickListener;
    private ScoreDAO scoreDAO;

    public ScoreAdapter(Context context, List<Score> scores, OnItemAdapterClickListener onItemAdapterClickListener) {
        this.context = context;
        this.scores = scores;
        this.onItemAdapterClickListener = onItemAdapterClickListener;
        this.scoreDAO = PokemonAppDatabase.getInstance(this.context).getScoreDAO();
    }

    @NonNull
    @Override
    public ScoreAdapter.ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View typeItem = LayoutInflater.from(context)
                .inflate(R.layout.score_adapter_layout,parent,false);
        return new ScoreAdapter.ScoreViewHolder(typeItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreAdapter.ScoreViewHolder holder, int position) {
        holder.bind(scores.get(position));
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }

    class ScoreViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView scoreDate;
        private TextView scoreValue;
        private TextView scoreGameInfo;
        private List<TextView> scorePlayerTeamPokemon = new ArrayList<>();
        private List<TextView> scoreCpuTeamPokemon = new ArrayList<>();

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreDate = itemView.findViewById(R.id.score_date);
            scoreValue = itemView.findViewById(R.id.score_value);
            scoreGameInfo = itemView.findViewById(R.id.score_game_info);
            scorePlayerTeamPokemon.add(itemView.findViewById(R.id.player_pokemon_1));
            scorePlayerTeamPokemon.add(itemView.findViewById(R.id.player_pokemon_2));
            scorePlayerTeamPokemon.add(itemView.findViewById(R.id.player_pokemon_3));
            scorePlayerTeamPokemon.add(itemView.findViewById(R.id.player_pokemon_4));
            scorePlayerTeamPokemon.add(itemView.findViewById(R.id.player_pokemon_5));
            scorePlayerTeamPokemon.add(itemView.findViewById(R.id.player_pokemon_6));
            scoreCpuTeamPokemon.add(itemView.findViewById(R.id.cpu_pokemon_1));
            scoreCpuTeamPokemon.add(itemView.findViewById(R.id.cpu_pokemon_2));
            scoreCpuTeamPokemon.add(itemView.findViewById(R.id.cpu_pokemon_3));
            scoreCpuTeamPokemon.add(itemView.findViewById(R.id.cpu_pokemon_4));
            scoreCpuTeamPokemon.add(itemView.findViewById(R.id.cpu_pokemon_5));
            scoreCpuTeamPokemon.add(itemView.findViewById(R.id.cpu_pokemon_6));
            itemView.setOnCreateContextMenuListener(this);
        }

        public void bind(Score score){
            this.itemView.setBackground(makeSelector(context.getResources().getColor(R.color.white),0.8f));
            this.scoreDate.setText(score.getDate());
            this.scoreValue.setText(context.getString(R.string.score_label)+" : "+score.getScoreValue().toString());

            String gameModeString = getGameModeStringFromMnemonic(context,score.getGameMode());
            String gameLevelString = getGameLevelStringFromMnemonic(context,score.getGameLevel());

            this.scoreGameInfo.setText(gameModeString+" - "+gameLevelString);
            List<InGamePokemon> playerTeam = getInGamePokemonFromJSON(score.getPlayerTeam());
            for (int i=0;i<scorePlayerTeamPokemon.size();i++){
                if (playerTeam != null) {
                    scorePlayerTeamPokemon.get(i).setText(playerTeam.get(i).getPokemonServer().getFName());
                }
            }
            List<InGamePokemon> cpuTeam = getInGamePokemonFromJSON(score.getCpuTeam());
            for (int i=0;i<scoreCpuTeamPokemon.size();i++){
                if (cpuTeam != null) {
                    scoreCpuTeamPokemon.get(i).setText(cpuTeam.get(i).getPokemonServer().getFName());
                }
            }

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemAdapterClickListener.onClick(v, score);
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // creates a context menu with a delete option triggered by a long click
            MenuItem delete = menu.add("Delete");
            delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    // shows a dialog to confirm that the user wants to delete a score register (avoids accidents)
                    AlertDialog confirmDeletionDialog = dualButtonDialog(context, context.getString(R.string.delete_score_dialog_title),
                            context.getString(R.string.delete_score_dialog_text),
                            context.getString(R.string.yes), context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ProgressDialog loadingDialog = loadingDialog(context);
                                    loadingDialog.show();
                                    Score selectedScore = (Score) scores.get(ScoreViewHolder.this.getBindingAdapterPosition());
                                    // deletes the clicked element from the database
                                    scoreDAO.deleteTask(selectedScore, new OnTaskListener() {
                                        @Override
                                        public void onTask() {
                                            // removes the clicked element from the recycler view list
                                            scores.remove(ScoreViewHolder.this.getBindingAdapterPosition());
                                            // communicates the adapter about the change of its list
                                            notifyItemRemoved(ScoreViewHolder.this.getBindingAdapterPosition());
                                            loadingDialog.dismiss();
                                            Toast.makeText(context, R.string.score_deleted_confirmation, Toast.LENGTH_LONG).show();
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
        }
    }

}
