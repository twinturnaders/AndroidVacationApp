package wgu.bright.d308.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import wgu.bright.d308.Views.VacationViews;
import wgu.bright.d308.adapters.ExcursionAdapter;
import wgu.bright.d308.databinding.VacationDetailActivityBinding;
import wgu.bright.d308.entities.Excursion;
import wgu.bright.d308.entities.Vacation;

@RequiresApi(api = Build.VERSION_CODES.O)
public class VacationDetailActivity extends AppCompatActivity {

    private VacationDetailActivityBinding binding;
    private VacationViews vacationViews;
    private Vacation vacation;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private ExcursionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = VacationDetailActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vacationViews = new ViewModelProvider(this).get(VacationViews.class);

        long vacationId = getIntent().getLongExtra("vacationId", 0);
        Log.e("VacationDetail", "VacationId Passed: " + vacationId);
        if (vacationId == 0) {
            Toast.makeText(this, "Invalid vacation ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // RecyclerView setup
        RecyclerView recyclerView = binding.recyclerViewExcursions;
        adapter = new ExcursionAdapter(new ArrayList<>(), excursion -> {
           Excursion selectedExcursion = excursion;
        });
        binding.recyclerViewExcursions.setAdapter(adapter);
        binding.recyclerViewExcursions.setLayoutManager(new LinearLayoutManager(this));

        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            vacation = vacationViews.getRepository().getVacationDao().getVacationById(vacationId);

            if (vacation == null) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Vacation not found.", Toast.LENGTH_SHORT).show();
                    finish();
                });
                return;
            }

            List<Excursion> excursions = vacationViews.getRepository()
                    .getExcursionDao().getExcursionsForVacation(vacationId);

            runOnUiThread(() -> {
                binding.textViewVacationTitle.setText(
                        vacation.title != null ? vacation.title : "Untitled Vacation"
                );
                binding.textViewHotel.setText(
                        vacation.hotel != null ? vacation.hotel : "No Hotel"
                );

                try {
                    LocalDate start = LocalDate.parse(vacation.startDate);
                    binding.textViewStartDate.setText(start.format(dateFormatter));
                } catch (Exception e) {
                    binding.textViewStartDate.setText("N/A");
                }

                try {
                    LocalDate end = LocalDate.parse(vacation.endDate);
                    binding.textViewEndDate.setText(end.format(dateFormatter));
                } catch (Exception e) {
                    binding.textViewEndDate.setText("N/A");
                }

                adapter.setExcursions(excursions);
            });
        });
        binding.buttonReturnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);//return home
            startActivity(intent);
        });

        binding.buttonEditVacation.setOnClickListener(v -> {
            Intent intent = new Intent(this, VacationActivity.class);//pass id to vacationActivity
            intent.putExtra("vacationId", vacation.id);
            startActivity(intent);
        });

        binding.buttonShareVacation.setOnClickListener(v -> {
            String title = vacation.title != null ? vacation.title : "Untitled Vacation";
            String hotel = vacation.hotel != null ? vacation.hotel : "No Hotel";
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
            startActivity(Intent.createChooser(shareIntent, "Share vacation via..."));
        });

        binding.buttonDeleteVacation.setOnClickListener(v -> {
            vacationViews.vacationDeleteCheck(vacation, canDelete -> runOnUiThread(() -> {
                if (canDelete) {
                    vacationViews.deleteVacation(vacation);
                    Toast.makeText(this, "Vacation deleted.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);//back to home screen
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Can't delete, excursions are attached!", Toast.LENGTH_SHORT).show();
                }
            }));
        });
    }
    private Vacation buildVacationFromFields() {
        Vacation vacation = new Vacation();
        vacation.id = vacationViews.getCurrentVacationId();
        vacation.title = binding.textViewVacationTitle.getText().toString().trim();
        vacation.hotel = binding.textViewHotel.getText().toString().trim();
        vacation.startDate = binding.textViewStartDate.getText().toString().trim();
        vacation.endDate = binding.textViewEndDate.getText().toString().trim();
        return vacation;
    }
}
