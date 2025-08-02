package wgu.bright.d308.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import wgu.bright.d308.Views.VacationViews;
import wgu.bright.d308.databinding.VacationDetailActivityBinding;
import wgu.bright.d308.entities.Vacation;

@RequiresApi(api = Build.VERSION_CODES.O)
public class VacationDetailActivity extends AppCompatActivity {

    private VacationDetailActivityBinding binding;
    private VacationViews vacationViews;
    private Vacation vacation;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = VacationDetailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vacationViews = new ViewModelProvider(this).get(VacationViews.class);
        int vacationId = getIntent().getIntExtra("vacationId", 0);

        vacation = new Vacation();
        vacation.id = vacationId;
        vacation.title = vacationViews.getVacationTitle().getValue();
        vacation.hotel = vacationViews.getVacationHotel().getValue();
        vacation.startDate = vacationViews.getVacationStartDate().getValue().toString();
        vacation.endDate = vacationViews.getVacationEndDate().getValue().toString();

        binding.textViewVacationTitle.setText(vacation.title);
        binding.textViewHotel.setText(vacation.hotel);
        binding.textViewStartDate.setText(LocalDate.parse(vacation.startDate).format(dateFormatter));
        binding.textViewEndDate.setText(LocalDate.parse(vacation.endDate).format(dateFormatter));

        binding.buttonEditVacation.setOnClickListener(v -> {
            finish();
        });

        binding.buttonShareVacation.setOnClickListener(v -> {
            String title = vacation.title != null ? vacation.title : "Untitled Vacation";
            String hotel = vacation.hotel != null ? vacation.hotel : "No Hotel Listed";
            String start = vacation.startDate != null ? vacation.startDate : "N/A";
            String end = vacation.endDate != null ? vacation.endDate : "N/A";

            String message = "Vacation Time!!\n\n"
                    + "Title: " + title + "\n"
                    + "Hotel: " + hotel + "\n"
                    + "Start Date: " + start + "\n"
                    + "End Date: " + end;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation: " + title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);

            startActivity(Intent.createChooser(shareIntent, "Share vacation via..."));
        });

        binding.buttonDeleteVacation.setOnClickListener(v -> {
            vacationViews.vacationDeleteCheck(vacation, canDelete -> runOnUiThread(() -> {
                if (canDelete) {
                    vacationViews.deleteVacation(vacation);
                    Toast.makeText(this, "Vacation deleted.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Can't delete, excursions are attached!", Toast.LENGTH_SHORT).show();
                }
            }));
        });
    }
}
