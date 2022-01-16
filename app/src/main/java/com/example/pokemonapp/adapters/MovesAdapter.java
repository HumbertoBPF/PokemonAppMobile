package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.Tools.listOfTypesAsString;
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
import com.example.pokemonapp.dao.MoveTypeDAO;
import com.example.pokemonapp.entities.Move;
import com.example.pokemonapp.entities.Type;
import com.example.pokemonapp.room.PokemonAppDatabase;

import java.util.ArrayList;
import java.util.List;

public class MovesAdapter extends RecyclerView.Adapter<MovesAdapter.MovesViewHolder> {

    private Context context;
    private List<Object> moves;
    private OnItemAdapterClickListener onItemAdapterClickListener;
    private MoveTypeDAO moveTypeDAO;

    public MovesAdapter(Context context, List<Object> moves, OnItemAdapterClickListener onItemAdapterClickListener){
        this.context = context;
        this.moves = moves;
        this.onItemAdapterClickListener = onItemAdapterClickListener;
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

        private View itemView;
        private CardView moveTypeContainer;
        private TextView moveName;
        private TextView moveType;
        private TextView moveCategory;
        private TextView movePower;
        private TextView moveAccuracy;
        private TextView movePp;

        public MovesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.moveTypeContainer = itemView.findViewById(R.id.move_type_container);
            this.moveName = itemView.findViewById(R.id.move_name);
            this.moveType = itemView.findViewById(R.id.move_type);
            this.moveCategory = itemView.findViewById(R.id.move_category);
            this.movePower = itemView.findViewById(R.id.move_power);
            this.moveAccuracy = itemView.findViewById(R.id.move_accuracy);
            this.movePp = itemView.findViewById(R.id.move_pp);
        }

        public void bind(Move move){
            this.itemView.setBackground(makeSelector(context.getResources().getColor(R.color.white),0.8f));
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
                    Type typeMove = (Type) objects.get(0);
                    moveTypeContainer.setCardBackgroundColor(Color.parseColor("#"+typeMove.getFColorCode()));
                }
            }).execute();
            this.moveCategory.setText(context.getResources().getString(R.string.label_category)+"\n"+move.getFCategory());
            this.movePower.setText(context.getResources().getString(R.string.power_label)+"\n"+move.getFPower().toString());
            this.moveAccuracy.setText(context.getResources().getString(R.string.accuracy_label)+"\n"+move.getFAccuracy());
            this.movePp.setText(context.getResources().getString(R.string.pp_label)+"\n"+move.getFPp());
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemAdapterClickListener.onClick(view, move);
                }
            });
        }

    }

}
