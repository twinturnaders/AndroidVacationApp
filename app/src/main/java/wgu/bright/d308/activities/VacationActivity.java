package wgu.bright.d308.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import wgu.bright.d308.Views.VacationViews;
import wgu.bright.d308.databinding.ActivityMainBinding;
import wgu.bright.d308.entities.Vacation;

public class VacationActivity extends AppCompatActivity {
    private VacationViews vacationViews;
    //bind to xml file for view
    private ActivityMainBinding binding;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding logic
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get views from class
        vacationViews = new ViewModelProvider(this).get(VacationViews.class);


        //Delete button click/ check logic
        binding.buttonDeleteVacation.setOnClickListener(v -> {
            Vacation vacation = buildVacationFromFields();

            vacationViews.vacationDeleteCheck(vacation, canDelete -> {
                runOnUiThread(() -> {

                    //successful delete
                    if (canDelete) {
                        vacationViews.deleteVacation(vacation);
                        Toast.makeText(this, "Vacation deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    //excursions attached
                    else {
                        Toast.makeText(this, "Can't delete — excursions are attached!", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }

    //getting all the forms from the model
        private Vacation buildVacationFromFields() {
            Vacation vacation = new Vacation();
            vacation.id = vacationViews.getCurrentVacationId();
            vacation.title = binding.editTextVacationTitle.getText().toString().trim();
            vacation.hotel = binding.editTextHotel.getText().toString().trim();
            vacation.startDate = binding.editTextStartDate.getText().toString().trim();
            vacation.endDate = binding.editTextEndDate.getText().toString().trim();
            return vacation;
        }
    }
