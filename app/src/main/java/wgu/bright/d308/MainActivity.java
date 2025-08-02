package wgu.bright.d308.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import wgu.bright.d308.VacationRepository;
import wgu.bright.d308.data.AppData;
import wgu.bright.d308.databinding.ActivityHomeBinding;
import wgu.bright.d308.entities.Vacation;

public class MainActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private VacationRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = new VacationRepository(getApplication());

        // 🔍 Search logic
        binding.buttonSearch.setOnClickListener(v -> {
            String idInput = binding.editTextSearchId.getText().toString().trim();
            String titleInput = binding.editTextSearchTitle.getText().toString().trim();

            if (TextUtils.isEmpty(idInput) && TextUtils.isEmpty(titleInput)) {
                Toast.makeText(this, "Enter ID or Title to search.", Toast.LENGTH_SHORT).show();
                return;
            }

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Vacation foundVacation = null;

                if (!idInput.isEmpty()) {
                    int id = Integer.parseInt(idInput);
                    foundVacation = repository.getVacationDao().getVacationById(id);
                } else if (!titleInput.isEmpty()) {
                    foundVacation = repository.getVacationDao().getVacationByTitle(titleInput);
                }

                Vacation finalFound = foundVacation;
                runOnUiThread(() -> {
                    if (finalFound != null) {
                        Intent intent = new Intent(this, VacationActivity.class);
                        intent.putExtra("vacationId", (int) finalFound.id); // assuming long id
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Vacation not found.", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

        // ➕ Create new vacation
        binding.buttonCreateVacation.setOnClickListener(v -> {
            Intent intent = new Intent(this, VacationActivity.class);
            startActivity(intent);
        });
    }
}
