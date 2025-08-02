package wgu.bright.d308.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import wgu.bright.d308.Views.ExcursionViews;
import wgu.bright.d308.databinding.ActivityExcursionBinding;

public class ExcursionActivity extends AppCompatActivity {

    private ExcursionViews excursionViews;
    private ActivityExcursionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcursionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        excursionViews = new ViewModelProvider(this).get(ExcursionViews.class);

        // Get vacation excursion is tied to from intent
        long vacationId = getIntent().getLongExtra("vacationId", 0);
        excursionViews.setVacationId(vacationId);

        // Bind to UI
        binding.buttonSaveExcursion.setOnClickListener(v -> {
            excursionViews.getExcursionTitle().setValue(
                    binding.editTextExcursionTitle.getText().toString().trim()
            );
            excursionViews.getExcursionDate().setValue(
                    binding.editTextExcursionDate.getText().toString().trim()
            );

            excursionViews.saveExcursion(id -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Excursion saved!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        });

        binding.buttonDeleteExcursion.setOnClickListener(v -> {
            excursionViews.deleteExcursion();
            Toast.makeText(this, "Excursion deleted.", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
