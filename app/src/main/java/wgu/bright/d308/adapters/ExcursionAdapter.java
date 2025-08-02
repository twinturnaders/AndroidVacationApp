package wgu.bright.d308.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wgu.bright.d308.R;
import wgu.bright.d308.entities.Excursion;

public class ExcursionAdapter extends RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder> {
    public interface OnExcursionClickListener {
        void onExcursionClick(Excursion excursion);
    }

    private List<Excursion> excursionList;
    private final OnExcursionClickListener listener;

    public ExcursionAdapter(List<Excursion> excursionList, OnExcursionClickListener listener) {
        this.excursionList = excursionList;
        this.listener = listener;
    }
    public ExcursionAdapter(List<Excursion> excursionList) {
        this.excursionList = excursionList;
        this.listener = null;
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
    }

    @Override
    public int getItemCount() {
        return excursionList != null ? excursionList.size() : 0;
    }

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
