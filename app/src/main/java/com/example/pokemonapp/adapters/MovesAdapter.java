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
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesAdapter extends RecyclerView.Adapter<MovesAdapter.MovesViewHolder> {

    private Context context;
    private List<Object> moves;
    private OnClickListener onClickListener;
    private MoveTypeDAO moveTypeDAO;

    public MovesAdapter(Context context, List<Object> moves, OnClickListener onClickListener){
        this.context = context;
        this.moves = moves;
        this.onClickListener = onClickListener;
        this.moveTypeDAO = PokemonAppDatabase.getInstance(this.context).getMoveTypeDAO();
    }

    @NonNull
    @Override
    public MovesAdapter.MovesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View moveItem = LayoutInflater.from(context)
                .inflate(R.layout.moves_adapter_layout,parent,false);
        return new MovesViewHolder(moveItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MovesAdapter.MovesViewHolder holder, int position) {
        holder.bind((Move) moves.get(position));
    }

    @Override
    public int getItemCount() {
        return moves.size();
    }

    class MovesViewHolder extends RecyclerView.ViewHolder{

        private View moveView;
        private TextView moveName;
        private TextView moveType;
        private TextView moveCategory;
        private TextView movePower;
        private TextView moveAccuracy;
        private TextView movePp;

        public MovesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.moveView = itemView;
            this.moveName = itemView.findViewById(R.id.move_name);
            this.moveType = itemView.findViewById(R.id.move_type);
            this.moveCategory = itemView.findViewById(R.id.move_category);
            this.movePower = itemView.findViewById(R.id.move_power);
            this.moveAccuracy = itemView.findViewById(R.id.move_accuracy);
            this.movePp = itemView.findViewById(R.id.move_pp);
        }

        public void bind(Move move){
            this.moveName.setText(move.getFName());
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
            this.moveCategory.setText(context.getResources().getString(R.string.category_move_label)+"\n"+move.getFCategory());
            this.movePower.setText(context.getResources().getString(R.string.power_move_label)+"\n"+move.getFPower().toString());
            this.moveAccuracy.setText(context.getResources().getString(R.string.accuracy_move_label)+"\n"+move.getFAccuracy());
            this.movePp.setText(context.getResources().getString(R.string.pp_move_label)+"\n"+move.getFPp());
            this.moveView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(view, move);
                }
            });
        }

    }

    public interface OnClickListener{
        void onClick(View view, Move move);
    }

}
