package wgu.bright.d308.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import wgu.bright.d308.Views.VacationViews;
import wgu.bright.d308.databinding.ActivityMainBinding;
import wgu.bright.d308.entities.Vacation;

public class VacationActivity extends AppCompatActivity {
    private VacationViews vacationViews;
    //bind to xml file for view
    private ActivityMainBinding binding;
    @SuppressLint("NewApi")
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //binding logic
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //get views from class
        vacationViews = new ViewModelProvider(this).get(VacationViews.class);

        // Hook up observers to update UI when LiveData changes
        vacationViews.getVacationStartDate().observe(this, date -> {
            if (date != null) {
                binding.editTextStartDate.setText(date.format(dateFormatter));
            }
        });

        vacationViews.getVacationEndDate().observe(this, date -> {
            if (date != null) {
                binding.editTextEndDate.setText(date.format(dateFormatter));
            }
        });

        // Show date pickers on click
        binding.editTextStartDate.setOnClickListener(v ->
                showDatePicker(vacationViews.getVacationStartDate(), binding.editTextStartDate));

        binding.editTextEndDate.setOnClickListener(v ->
                showDatePicker(vacationViews.getVacationEndDate(), binding.editTextEndDate));

        // Save vacation
        binding.buttonSaveVacation.setOnClickListener(v -> {
            vacationViews.getVacationTitle().setValue(binding.editTextVacationTitle.getText().toString().trim());
            vacationViews.getVacationHotel().setValue(binding.editTextHotel.getText().toString().trim());
            vacationViews.saveVacation();
            Toast.makeText(this, "Vacation saved!", Toast.LENGTH_SHORT).show();
        });


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
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePicker(MutableLiveData<LocalDate> targetLiveData, android.widget.EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            LocalDate selectedDate = null;
            selectedDate = LocalDate.of(y, m + 1, d);
            targetLiveData.setValue(selectedDate);
            targetEditText.setText(selectedDate.format(dateFormatter));
        }, year, month, day);

        dialog.show();
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
