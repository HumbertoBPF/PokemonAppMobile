package com.example.pokemonapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.models.Move;

import java.util.List;

public class MovesAdapter extends RecyclerView.Adapter<MovesAdapter.MovesViewHolder> {

    private Context context;
    private List<Move> moves;

    public MovesAdapter(Context context, List<Move> moves){
        this.context = context;
        this.moves = moves;
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
        holder.bind(moves.get(position));
    }

    @Override
    public int getItemCount() {
        return moves.size();
    }

    class MovesViewHolder extends RecyclerView.ViewHolder{

        private TextView moveName;
        private TextView moveType;
        private TextView moveCategory;
        private TextView movePower;
        private TextView moveAccuracy;
        private TextView movePp;

        public MovesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.moveName = itemView.findViewById(R.id.move_name);
            this.moveType = itemView.findViewById(R.id.move_type);
            this.moveCategory = itemView.findViewById(R.id.move_category);
            this.movePower = itemView.findViewById(R.id.move_power);
            this.moveAccuracy = itemView.findViewById(R.id.move_accuracy);
            this.movePp = itemView.findViewById(R.id.move_pp);
        }

        public void bind(Move move){
            this.moveName.setText(move.getFName());
            this.moveType.setText("Move type");
            this.moveCategory.setText(context.getResources().getString(R.string.category_move_label)+"\n"+move.getFCategory());
            this.movePower.setText(context.getResources().getString(R.string.power_move_label)+"\n"+move.getFPower().toString());
            this.moveAccuracy.setText(context.getResources().getString(R.string.accuracy_move_label)+"\n"+move.getFAccuracy());
            this.movePp.setText(context.getResources().getString(R.string.pp_move_label)+"\n"+move.getFPp());
        }

    }

}
