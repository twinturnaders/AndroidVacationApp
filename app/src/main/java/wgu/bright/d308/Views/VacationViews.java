package wgu.bright.d308.Views;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Consumer;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import wgu.bright.d308.VacationRepository;
import wgu.bright.d308.entities.Vacation;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
@RequiresApi(api = Build.VERSION_CODES.O)
public class VacationViews extends AndroidViewModel {

    public VacationRepository getRepository() {
        return repository;
    }

    private final VacationRepository repository;


    private final MutableLiveData<String> vacationTitle = new MutableLiveData<>();
    private final MutableLiveData<String> vacationHotel = new MutableLiveData<>();

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    private final MutableLiveData<String> phoneNumber = new MutableLiveData<>();

    public MutableLiveData<LocalDate> getVacationEndDate() {
        return vacationEndDate;
    }

    public MutableLiveData<LocalDate> getVacationStartDate() {
        return vacationStartDate;
    }

    private final MutableLiveData<LocalDate> vacationEndDate = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> vacationStartDate = new MutableLiveData<>();


    public long getCurrentVacationId() {
        return currentVacationId;
    }

    private long currentVacationId = 0;

    public VacationViews(@NonNull Application application) {
        super(application);
        repository = new VacationRepository(application);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void setEditingVacation(Vacation vacation) {
        currentVacationId = vacation.id;
        vacationTitle.setValue(vacation.title);
        vacationHotel.setValue(vacation.hotel);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vacationStartDate.setValue(LocalDate.parse(vacation.startDate));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vacationEndDate.setValue(LocalDate.parse(vacation.endDate));
        }

    }

    public MutableLiveData<String> getVacationTitle() { return vacationTitle; }
    public MutableLiveData<String> getVacationHotel() { return vacationHotel; }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveVacation(Runnable onComplete) {
        Vacation vacation = new Vacation();
        vacation.title = vacationTitle.getValue();
        vacation.phone = phoneNumber.getValue();
        vacation.hotel = vacationHotel.getValue();

        LocalDate start = vacationStartDate.getValue();
        LocalDate end = vacationEndDate.getValue();

        if (start == null || end == null) return;

        if (!end.isAfter(start)) {
            Log.e("Validation", "End date must be after start date");
            Toast.makeText(this.getApplication(), "End date must be after start date... unless you're travelling through time, then send me a message, we'll talk", Toast.LENGTH_LONG).show();
            return;
        }


        vacation.startDate = start.toString(); // Format: 2025-08-03
        vacation.endDate = end.toString();

        if (currentVacationId == 0) {
            repository.insertVacation(vacation, newId -> {
                currentVacationId = newId;
                Log.d("InsertedVacation", "Vacation saved with ID: " + newId);
                onComplete.run();
            });
        } else {
            vacation.id = currentVacationId;
            repository.updateVacation(vacation);
            onComplete.run();
        }
    }

    //pull method into current vacation views
    public void vacationDeleteCheck(Vacation vacation, Consumer<Boolean> callback){
        vacation.id = currentVacationId;
        repository.canDelete(vacation, callback);




    }

    public void deleteVacation(Vacation vacation) {
        if (currentVacationId == 0) return;
        vacation.id = currentVacationId;
        vacation.title = vacationTitle.getValue();
        vacation.hotel = vacationHotel.getValue();
        vacation.startDate = String.valueOf(vacationStartDate.getValue());
        vacation.endDate = String.valueOf(vacationEndDate.getValue());
        repository.deleteVacation(vacation);
        currentVacationId = 0;
    }



}
