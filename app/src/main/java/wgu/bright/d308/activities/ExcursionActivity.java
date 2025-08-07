package wgu.bright.d308.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import wgu.bright.d308.Views.ExcursionViews;
import wgu.bright.d308.alerts.AlertReceiver;
import wgu.bright.d308.data.AppData;
import wgu.bright.d308.databinding.ActivityExcursionBinding;
import wgu.bright.d308.entities.Excursion;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExcursionActivity extends AppCompatActivity {

    private ExcursionViews excursionViews;
    private ActivityExcursionBinding binding;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    String originalTitle;
    LocalDate originalDate;
    private LocalDate vacationStart;
    private LocalDate vacationEnd;
    private LocalDate excursionDate;
    private String excursionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcursionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        excursionViews = new ViewModelProvider(this).get(ExcursionViews.class);

        // Load intent extras
        long vacationId = getIntent().getLongExtra("vacationId", 0L);
        long excursionId = getIntent().getLongExtra("excursionId", 0L);
        String startDateString = getIntent().getStringExtra("vacationStart");
        String endDateString = getIntent().getStringExtra("vacationEnd");

        excursionViews.setVacationId(vacationId);

        // Parse vacation date boundaries
        try {
            if (startDateString != null) vacationStart = LocalDate.parse(startDateString);
            if (endDateString != null) vacationEnd = LocalDate.parse(endDateString);
        } catch (Exception e) {
            Toast.makeText(this, "Invalid vacation dates", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        // Load excursion if editing
        if (excursionId > 0L) {
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                Excursion excursion = AppData.getDatabase(getApplicationContext()).excursionDao().getExcursionById(excursionId);
                if (excursion != null) {
                    runOnUiThread(() -> {
                        excursionTitle = excursion.title;
                        binding.editTextExcursionTitle.setText(excursionTitle);
                        originalTitle = excursionTitle;


                        if (!excursion.date.isEmpty()) {
                            try {
                                excursionDate = LocalDate.parse(excursion.date);
                                excursionViews.getExcursionDate().setValue(excursionDate);
                                binding.editTextExcursionDate.setText(excursionDate.format(dateFormatter));
                                originalDate = excursionDate;
                            } catch (Exception e) {
                                Toast.makeText(this, "Failed to parse excursion date", Toast.LENGTH_LONG).show();
                                Log.e("ExcursionActivity", "Bad date: " + excursion.date);
                            }
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Excursion not found", Toast.LENGTH_SHORT).show());
                }
            });
        }

        binding.editTextExcursionDate.setOnClickListener(v -> showDatePicker(binding.editTextExcursionDate));

        binding.buttonSaveExcursion.setOnClickListener(v -> {
            excursionTitle = binding.editTextExcursionTitle.getText().toString().trim();
            String dateInput = binding.editTextExcursionDate.getText().toString().trim();


            if (excursionTitle.isEmpty() || dateInput.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                excursionDate = LocalDate.parse(dateInput, dateFormatter);
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format. Use MM/dd/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            if (vacationStart != null && vacationEnd != null) {
                if (excursionDate.isBefore(vacationStart) || excursionDate.isAfter(vacationEnd)) {
                    Toast.makeText(this, "Excursion must be within vacation dates.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            excursionViews.getExcursionTitle().setValue(excursionTitle);
            excursionViews.getExcursionDate().setValue(excursionDate);

            if(Objects.equals(excursionDate, originalDate) && excursionId != 0 && Objects.equals(excursionTitle, originalTitle)){
                Toast.makeText(this, "Excursion saved", Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                excursionViews.saveExcursion(id -> runOnUiThread(() -> {
                    Toast.makeText(this, "Excursion saved!", Toast.LENGTH_SHORT).show();

                }));
            }
        });

        binding.buttonCreateAlert.setOnClickListener(v -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    if (!alarmManager.canScheduleExactAlarms()) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                        startActivity(intent);
                        Toast.makeText(this, "Please grant exact alarm permission", Toast.LENGTH_LONG).show();
                        return;
                    }
                }



                if (excursionTitle.isEmpty() || excursionDate == null) {
                    Log.e("title check", "title received" + excursionTitle + "and date" + excursionDate);
                    Toast.makeText(this, "Missing excursion title or date", Toast.LENGTH_SHORT).show();
                    return;
                }

                int requestCode = (int) (300 + excursionViews.getCurrentExcursionId());
                scheduleAlert(excursionTitle, "Excursion Day", excursionDate, requestCode);
                Toast.makeText(this, "Alert set and will remind you on Excursion day!", Toast.LENGTH_SHORT).show();
            });


        binding.buttonDeleteExcursion.setOnClickListener(v -> {
            excursionViews.deleteExcursion();
            Toast.makeText(this, "Excursion deleted.", Toast.LENGTH_SHORT).show();
            finish();


        });

        //back to vacation button
        binding.buttonBackToVacation.setOnClickListener(v -> {
            finish();
        });

        //button return home

        binding.buttonReturnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }


    private void showDatePicker(EditText targetEditText) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, y, m, d) -> {
            LocalDate selectedDate = LocalDate.of(y, m + 1, d);
            excursionViews.getExcursionDate().setValue(selectedDate);
            targetEditText.setText(selectedDate.format(dateFormatter));
        }, year, month, day);

        dialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleAlert(String title, String type, LocalDate date, int requestCode) {
        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("type", type);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }

}
