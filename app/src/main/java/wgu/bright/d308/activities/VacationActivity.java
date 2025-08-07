package wgu.bright.d308.activities;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import wgu.bright.d308.Views.ExcursionViews;
import wgu.bright.d308.Views.VacationViews;
import wgu.bright.d308.adapters.ExcursionAdapter;
import wgu.bright.d308.alerts.AlertReceiver;
import wgu.bright.d308.databinding.ActivityMainBinding;
import wgu.bright.d308.entities.Excursion;
import wgu.bright.d308.entities.Vacation;

@RequiresApi(api = Build.VERSION_CODES.O)
public class VacationActivity extends AppCompatActivity {
    private VacationViews vacationViews;
    private ActivityMainBinding binding;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private ExcursionAdapter adapter;

    private Excursion selectedExcursion;
    private long vacationId;

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vacationViews = new ViewModelProvider(this).get(VacationViews.class);

        vacationViews.getVacationTitle().observe(this, title ->
                binding.editTextVacationTitle.setText(title));

        vacationViews.getVacationHotel().observe(this, hotel ->
                binding.editTextHotel.setText(hotel));

        vacationViews.getPhoneNumber().observe(this, phone ->
                binding.editTextPhoneNumber.setText(phone));
        String savedPhone = getIntent().getStringExtra("phoneNumber");

        if (savedPhone != null){
            binding.editTextPhoneNumber.setText(savedPhone);
        }
        vacationId = getIntent().getLongExtra("vacationId", 0L);
        if (vacationId != 0) {
            Executor executor = newSingleThreadExecutor();
            executor.execute(() -> {
                Vacation vacation = vacationViews.getRepository().getVacationDao().getVacationById(vacationId);
                if (vacation != null) {

                    runOnUiThread(() ->
                            vacationViews.setEditingVacation(vacation));
                    binding.editTextPhoneNumber.setText(vacation.phone.toString());
                    binding.buttonAddExcursion.setVisibility(View.VISIBLE);
                    binding.buttonEditExcursions.setVisibility(View.VISIBLE);
                    binding.buttonDeleteExcursion.setVisibility(View.VISIBLE);
                }
            });
        }

        adapter = new ExcursionAdapter(new ArrayList<>(), excursion -> {
            selectedExcursion = excursion;

        });
        binding.recyclerViewExcursions.setAdapter(adapter);
        binding.recyclerViewExcursions.setLayoutManager(new LinearLayoutManager(this));

        vacationViews.getVacationStartDate().observe(this, date -> {
            if (date != null) binding.editTextStartDate.setText(date.format(dateFormatter));
        });
        vacationViews.getVacationEndDate().observe(this, date -> {
            if (date != null) binding.editTextEndDate.setText(date.format(dateFormatter));
        });

        binding.editTextStartDate.setOnClickListener(v -> showDatePicker(vacationViews.getVacationStartDate(), binding.editTextStartDate));
        binding.editTextEndDate.setOnClickListener(v -> showDatePicker(vacationViews.getVacationEndDate(), binding.editTextEndDate));

        binding.buttonSaveVacation.setOnClickListener(v -> {
                    vacationViews.getVacationTitle().setValue(binding.editTextVacationTitle.getText().toString().trim());
                    vacationViews.getVacationHotel().setValue(binding.editTextHotel.getText().toString().trim());
                    vacationViews.getPhoneNumber().setValue(binding.editTextPhoneNumber.getText().toString().trim());

                    vacationViews.saveVacation(() -> runOnUiThread(() -> {
                        Toast.makeText(this, "Vacation saved!", Toast.LENGTH_SHORT).show();
                        binding.buttonAddExcursion.setVisibility(View.VISIBLE);
                        binding.buttonEditExcursions.setVisibility(View.VISIBLE);
                        binding.buttonDeleteExcursion.setVisibility(View.VISIBLE);


                    }));
                });

        binding.buttonShareVacation.setOnClickListener(v -> {
            String title = String.valueOf(vacationViews.getVacationTitle().getValue());
            String hotel = String.valueOf(vacationViews.getVacationHotel().getValue());
            String start = String.valueOf(vacationViews.getVacationStartDate().getValue());
            String end = String.valueOf(vacationViews.getVacationEndDate().getValue());

            String message = "Vacation Time!!\n\nTitle: " + title + "\nHotel: " + hotel + "\nStart Date: " + start + "\nEnd Date: " + end;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Vacation: " + title);
            startActivity(Intent.createChooser(shareIntent, "Share vacation via..."));
        });

        binding.buttonViewVacationDetails.setOnClickListener(v -> {
            long id = vacationViews.getCurrentVacationId();
            if (id == 0) {
                Toast.makeText(this, "Please save a vacation first.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, VacationDetailActivity.class);
                intent.putExtra("vacationId", id);
                startActivity(intent);
            }
        });

        binding.buttonCreateAlert.setOnClickListener(v -> {

            LocalDate start = vacationViews.getVacationStartDate().getValue();
            LocalDate end = vacationViews.getVacationEndDate().getValue();
            String title = vacationViews.getVacationTitle().getValue();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (!alarmManager.canScheduleExactAlarms()) {
                    Toast.makeText(this, "Exact alarm permission not granted", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (start != null) scheduleAlert(title, "starting", start, 100 + vacationViews.getCurrentVacationId());
            if (end != null) scheduleAlert(title, "ending", end, 200 + vacationViews.getCurrentVacationId());

        });
        binding.buttonDeleteVacation.setOnClickListener(v -> {
            Vacation vacation = buildVacationFromFields();
            vacationViews.vacationDeleteCheck(vacation, canDelete -> runOnUiThread(() -> {
                if (canDelete) {
                    vacationViews.deleteVacation(vacation);
                    binding.editTextPhoneNumber.setText("");
                    binding.editTextStartDate.setText("");
                    binding.editTextHotel.setText("");
                    binding.editTextEndDate.setText("");
                    binding.buttonAddExcursion.setVisibility(View.GONE);
                    binding.buttonEditExcursions.setVisibility(View.GONE);
                    binding.buttonDeleteExcursion.setVisibility(View.GONE);
                    Toast.makeText(this, "Vacation deleted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Can't delete — excursions are attached!", Toast.LENGTH_SHORT).show();
                }
            }));
        });

        binding.buttonHome.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("phoneNumber", savedPhone);
        startActivity(intent);
        });

        binding.buttonAddExcursion.setOnClickListener(v -> {
            Intent intent = new Intent(this, ExcursionActivity.class);
            intent.putExtra("vacationId", vacationViews.getCurrentVacationId());
            intent.putExtra("vacationStart", vacationViews.getVacationStartDate().getValue().toString());
            intent.putExtra("vacationEnd", vacationViews.getVacationEndDate().getValue().toString());
            startActivity(intent);
        });

        binding.buttonDeleteExcursion.setOnClickListener(v -> {
            if (selectedExcursion != null) {
                Executor executor = newSingleThreadExecutor();
                executor.execute(() -> {
                    vacationViews.getRepository().getExcursionDao().delete(selectedExcursion);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Excursion deleted!", Toast.LENGTH_SHORT).show();
                        selectedExcursion = null;
                        onResume();
                    });
                });
            } else {
                Toast.makeText(this, "No excursion selected.", Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonEditExcursions.setOnClickListener(v -> {
            if (selectedExcursion != null) {
                Intent intent = new Intent(this, ExcursionActivity.class);

                intent.putExtra("excursionId", selectedExcursion.id);

//                intent.putExtra("vacationStart", vacationViews.getVacationStartDate().getValue().toString());
//                intent.putExtra("vacationEnd", vacationViews.getVacationEndDate().getValue().toString());
                intent.putExtra("vacationId", vacationViews.getCurrentVacationId());
               intent.putExtra("vacationStart", String.valueOf(vacationViews.getVacationStartDate().getValue()));
                intent.putExtra("vacationEnd", String.valueOf(vacationViews.getVacationEndDate().getValue()));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No excursion selected.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker(MutableLiveData<LocalDate> targetLiveData, EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            LocalDate selectedDate = LocalDate.of(y, m + 1, d);
            targetLiveData.setValue(selectedDate);
            targetEditText.setText(selectedDate.format(dateFormatter));
        }, year, month, day);

        dialog.show();
    }

    private Vacation buildVacationFromFields() {
        Vacation vacation = new Vacation();
        vacation.id = vacationViews.getCurrentVacationId();
        vacation.title = binding.editTextVacationTitle.getText().toString().trim();
        vacation.phone = binding.editTextPhoneNumber.getText().toString().trim();
        vacation.hotel = binding.editTextHotel.getText().toString().trim();
        vacation.startDate = binding.editTextStartDate.getText().toString().trim();
        vacation.endDate = binding.editTextEndDate.getText().toString().trim();
        return vacation;
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private void scheduleAlert(String title, String type, LocalDate date, long requestCode) {
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("type", type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Executor executor = newSingleThreadExecutor();
        executor.execute(() -> {
            List<Excursion> excursions = vacationViews.getRepository()
                    .getExcursionDao().getExcursionsForVacation(vacationId);
            Log.d("ExcursionLoad", "Loaded " + excursions.size() + " excursions");
            runOnUiThread(() -> adapter.setExcursions(excursions));
        });
    }
}
