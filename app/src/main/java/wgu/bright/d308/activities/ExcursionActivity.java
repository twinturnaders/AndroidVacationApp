package wgu.bright.d308.activities;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import java.util.concurrent.Executor;

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

    private Excursion excursion;

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
        excursionViews.setExcursionId(excursionId);

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
        if (excursionId != 0L) {
            Executor executor = newSingleThreadExecutor();
            executor.execute(() -> {
                Excursion ex = AppData.getDatabase(getApplicationContext()).excursionDao().getExcursionById(excursionId);
                if (ex != null) {
                    runOnUiThread(() -> {
                        excursionViews.setEditingExcursion(ex);
                        binding.editTextExcursionTitle.setText(ex.title);
                        LocalDate parsed = LocalDate.parse(ex.date);
                        binding.editTextExcursionDate.setText(parsed.format(dateFormatter));
                        excursionTitle = ex.title;
                        excursionDate = parsed;
                        originalTitle = excursionTitle;
                        originalDate = excursionDate;
                    });
                }
            });
        }




        binding.editTextExcursionDate.setOnClickListener(v -> {
            showDatePicker(binding.editTextExcursionDate);

        });

        binding.buttonSaveExcursion.setOnClickListener(v -> {
            String titleInput = binding.editTextExcursionTitle.getText().toString().trim();
            String dateInput = binding.editTextExcursionDate.getText().toString().trim();

            if (titleInput.isEmpty() || dateInput.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate parsedDate;
            try {
                parsedDate = LocalDate.parse(dateInput, dateFormatter);
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format. Use MM/dd/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            if (vacationStart != null && vacationEnd != null) {
                if (parsedDate.isBefore(vacationStart) || parsedDate.isAfter(vacationEnd)) {
                    Toast.makeText(this, "Excursion must be within vacation dates.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            excursionTitle = titleInput;
            excursionDate = parsedDate;
            excursionViews.getExcursionTitle().setValue(excursionTitle);
            excursionViews.getExcursionDate().setValue(excursionDate);

            excursionViews.saveExcursion(id -> runOnUiThread(() -> {


            }));

        });

        binding.buttonCreateAlert.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                if (am != null && !am.canScheduleExactAlarms()) {

                    requestExactAlarmPermissionIfNeeded();

                }
            }


            String titleInput = binding.editTextExcursionTitle.getText().toString().trim();
            String dateInput = binding.editTextExcursionDate.getText().toString().trim();
            if (titleInput.isEmpty() || dateInput.isEmpty()) {
                Log.e("title check", "title received " + titleInput + " and date " + dateInput);
                Toast.makeText(this, "Missing excursion title or date", Toast.LENGTH_SHORT).show();
                return;
            }
            LocalDate parsedDate;
            try {
                parsedDate = LocalDate.parse(dateInput, dateFormatter);
            } catch (Exception e) {
                Toast.makeText(this, "Missing excursion title or date", Toast.LENGTH_SHORT).show();
                return;
            }

            int requestCode = (int) (300 + excursionViews.getCurrentExcursionId());
            scheduleAlert(titleInput, "Excursion Day", parsedDate, requestCode);

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
            excursionDate = selectedDate;
        }, year, month, day);

        dialog.show();
    }

    @SuppressLint("ScheduleExactAlarm")

    private void requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API 31+
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) {
                Intent i = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                i.setData(Uri.parse("package:" + getPackageName()));
                startActivity(i);
                Toast.makeText(this, "Enable \"Allow exact alarms\" to set precise alerts.", Toast.LENGTH_LONG).show();
            }
        }
    }
    private void scheduleAlert(String title, String type, LocalDate date, long requestCode) {
        long triggerTime = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();

        Intent intent = new Intent(this, AlertReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("type", type);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                (int) requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (am == null) {
            Toast.makeText(this, "Alarm service unavailable", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {

                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
                } else {

                    requestExactAlarmPermissionIfNeeded();
                    return;
                }
            } else {

                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pi);
            }

            Toast.makeText(this, "Alert set for " + date, Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Alert set and will remind you on Excursion day!", Toast.LENGTH_SHORT).show();
        } catch (SecurityException se) {

            Intent showIntent = new Intent(this, MainActivity.class);
            PendingIntent showPi = PendingIntent.getActivity(
                    this,
                    (int) requestCode,
                    showIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            AlarmManager.AlarmClockInfo clockInfo = new AlarmManager.AlarmClockInfo(triggerTime, showPi);
            am.setAlarmClock(clockInfo, pi);
            Toast.makeText(this, "Scheduled with Alarm Clock", Toast.LENGTH_LONG).show();
        }
    }


}
