package wgu.bright.d308.activities;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import wgu.bright.d308.Views.ExcursionViews;
import wgu.bright.d308.alerts.AlertReceiver;
import wgu.bright.d308.databinding.ActivityExcursionBinding;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExcursionActivity extends AppCompatActivity {

    private ExcursionViews excursionViews;
    private ActivityExcursionBinding binding;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityExcursionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        excursionViews = new ViewModelProvider(this).get(ExcursionViews.class);

        long vacationId = getIntent().getLongExtra("vacationId", 0);
        excursionViews.setVacationId(vacationId);

        // Get vacation bounds from intent
        LocalDate vacationStart;
        LocalDate vacationEnd;
// Show date pickers on click
        binding.editTextExcursionDate.setOnClickListener(v ->
                showDatePicker(excursionViews.getExcursionDate(), binding.editTextExcursionDate));
        String startSt = getIntent().getStringExtra("vacationStart");
        if (startSt != null) vacationStart = LocalDate.parse(startSt);
        else {
            vacationStart = null;
        }

        String endSt = getIntent().getStringExtra("vacationEnd");
        if (endSt != null) vacationEnd = LocalDate.parse(endSt);
        else {
            vacationEnd = null;
        }

        binding.buttonSaveExcursion.setOnClickListener(v -> {
            String title = binding.editTextExcursionTitle.getText().toString().trim();
            String rawDate = binding.editTextExcursionDate.getText().toString().trim();

            if (title.isEmpty() || rawDate.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate excursionDate;
            try {
                excursionDate = LocalDate.parse(rawDate, dateFormatter);
            } catch (Exception e) {
                Toast.makeText(this, "Invalid date format. Use MM/dd/yyyy", Toast.LENGTH_SHORT).show();
                return;
            }

            if (vacationStart != null && vacationEnd != null &&
                    (excursionDate.isBefore(vacationStart) || excursionDate.isAfter(vacationEnd))) {
                Toast.makeText(this, "Excursion must be within vacation dates.", Toast.LENGTH_SHORT).show();
                return;
            }

            excursionViews.getExcursionTitle().setValue(title);
            excursionViews.getExcursionDate().setValue(LocalDate.parse(rawDate));

            excursionViews.saveExcursion(id -> {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Excursion saved!", Toast.LENGTH_SHORT).show();
                    scheduleAlert(title, "Excursion starting", excursionDate, 300 + (int)excursionViews.getExcursionId());
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

    // ✨ Set alarm for excursion
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
    private void showDatePicker(MutableLiveData<LocalDate> targetLiveData, EditText targetEditText) {
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
}
