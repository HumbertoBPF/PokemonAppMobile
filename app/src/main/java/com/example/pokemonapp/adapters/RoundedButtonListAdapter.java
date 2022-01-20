package com.example.pokemonapp.adapters;

import static com.example.pokemonapp.util.UiTools.makeSelector;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokemonapp.R;
import com.example.pokemonapp.models.RoundedButton;

import java.util.List;

public class RoundedButtonListAdapter extends RecyclerView.Adapter<RoundedButtonListAdapter.RoundedButtonViewHolder>{

    private Context context;
    private List<RoundedButton> buttons;

    public RoundedButtonListAdapter(Context context, List<RoundedButton> buttons) {
        this.context = context;
        this.buttons = buttons;
    }

    @NonNull
    @Override
    public RoundedButtonListAdapter.RoundedButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View buttonView = LayoutInflater.from(context)
                .inflate(R.layout.rounded_button_adapter_layout,parent,false);
        return new RoundedButtonViewHolder(buttonView);
    }

    @Override
    public void onBindViewHolder(@NonNull RoundedButtonListAdapter.RoundedButtonViewHolder holder, int position) {
        holder.bind(buttons.get(position));
    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }

    class RoundedButtonViewHolder extends RecyclerView.ViewHolder{

        private View itemView;
        private CardView buttonParent;
        private TextView textView;

        public RoundedButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.buttonParent = itemView.findViewById(R.id.button_parent);
            this.textView = itemView.findViewById(R.id.text_view);
        }

        public void bind(RoundedButton roundedButton){
            this.buttonParent.setBackground(makeSelector(roundedButton.getColor(),0.8f));
            this.textView.setText(roundedButton.getText());
            this.itemView.setOnClickListener(roundedButton.getOnClickListener());
        }
    }

}
