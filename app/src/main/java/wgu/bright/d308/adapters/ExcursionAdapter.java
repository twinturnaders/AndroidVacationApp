package wgu.bright.d308.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import wgu.bright.d308.R;
import wgu.bright.d308.entities.Excursion;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    public ExcursionAdapter(ArrayList<Excursion> excursionList, OnExcursionClickListener listener) {
        this.listener = listener;
    }

    public interface OnExcursionClickListener {
        void onExcursionClick(Excursion excursion);
    }

    private List<Excursion> excursionList;
    private final OnExcursionClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ExcursionAdapter(List<Excursion> excursionList, OnExcursionClickListener listener) {
        this.excursionList = excursionList;
        this.listener = listener;

    }
   



    @NonNull
    @Override
    public ExcursionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_excursion, parent, false);
        return new ExcursionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExcursionViewHolder holder, int position) {
        Excursion excursion = excursionList.get(position);
        holder.titleTextView.setText(excursion.title);
        holder.dateTextView.setText(excursion.date);

        // let's actually set the holder and make it visible when clicked
        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(com.google.android.material.R.color.design_default_color_on_primary));
        } else {
            holder.itemView.setBackgroundColor(holder.itemView.getContext().getColor(android.R.color.transparent));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousPosition); // reset previous
            notifyItemChanged(selectedPosition); // highlight new

            if (listener != null) {
                listener.onExcursionClick(excursion);
            }
        });
    }


    @Override
    public int getItemCount() {
        return excursionList != null ? excursionList.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setExcursions(List<Excursion> newList) {
        this.excursionList = newList;
        notifyDataSetChanged();
    }

    static class ExcursionViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;

        public ExcursionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewExcursionTitle);
            dateTextView = itemView.findViewById(R.id.textViewExcursionDate);
        }
    }
}
