package wgu.bright.d308.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import wgu.bright.d308.R;
import wgu.bright.d308.entities.Vacation;

public class VacationAdapter extends RecyclerView.Adapter<VacationAdapter.VacationViewHolder> {
    public interface VacationClickListener {
        void onVacationClick(Vacation vacation);
    }

    private List<Vacation> vacations;
    private final VacationClickListener listener;

    public VacationAdapter(List<Vacation> vacations, VacationClickListener listener) {
        this.vacations = vacations;
        this.listener = listener;
    }

    public void setVacations(List<Vacation> vacations) {
        this.vacations = vacations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VacationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vacation, parent, false);
        return new VacationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VacationViewHolder holder, int position) {
        Vacation vacation = vacations.get(position);
        holder.title.setText(vacation.title);
        holder.dates.setText(vacation.startDate + " to " + vacation.endDate);
        holder.hotel.setText(vacation.hotel);

        holder.itemView.setOnClickListener(v -> listener.onVacationClick(vacation));
    }

    @Override
    public int getItemCount() {
        return vacations != null ? vacations.size() : 0;
    }

    static class VacationViewHolder extends RecyclerView.ViewHolder {
        TextView title, dates, hotel;

        public VacationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textVacationTitle);
            dates = itemView.findViewById(R.id.textVacationDates);
            hotel = itemView.findViewById(R.id.textVacationHotel);
        }
    }
}
