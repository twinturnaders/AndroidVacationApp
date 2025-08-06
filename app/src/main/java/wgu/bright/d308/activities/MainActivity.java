package wgu.bright.d308.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import wgu.bright.d308.VacationRepository;
import wgu.bright.d308.adapters.VacationAdapter;
import wgu.bright.d308.databinding.ActivityHomeBinding;
import wgu.bright.d308.entities.Vacation;

public class MainActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private VacationRepository repository;
    private VacationAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new VacationRepository(getApplication());

        // Set up RecyclerView with adapter
        adapter = new VacationAdapter(new java.util.ArrayList<>(), vacation -> {
            Intent intent = new Intent(this, VacationDetailActivity.class);//start from vacationDetail
            intent.putExtra("vacationId", vacation.id);
            startActivity(intent);
        });
        binding.recyclerViewVacations.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewVacations.setAdapter(adapter);
        binding.recyclerViewVacations.setVisibility(View.GONE);

        // Create new vacation
        binding.buttonCreateVacation.setOnClickListener(v -> {
            Intent intent = new Intent(this, VacationActivity.class);
            startActivity(intent);
        });

        //Search by phone number
        binding.buttonSearch.setOnClickListener(v -> {
            String phone = binding.editTextSearch.getText().toString().trim();
            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter a phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            executor.execute(() -> {
                List<Vacation> matches = repository.getVacationDao().getVacationsByPhone(phone);
                runOnUiThread(() -> {
                    if (matches != null && !matches.isEmpty()) {
                        adapter.setVacations(matches);
                        binding.recyclerViewVacations.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(this, "No vacations found.", Toast.LENGTH_SHORT).show();
                        binding.recyclerViewVacations.setVisibility(View.GONE);
                    }
                });
            });
        });
    }
}
