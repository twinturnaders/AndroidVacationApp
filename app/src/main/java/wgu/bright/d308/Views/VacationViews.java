package wgu.bright.d308.Views;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import java.time.LocalDate;
import java.util.Date;
import java.util.function.Consumer;
import android.app.Application;
import android.os.Build;
import android.util.Log;

import wgu.bright.d308.VacationRepository;
import wgu.bright.d308.entities.Vacation;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class VacationViews extends AndroidViewModel {

    private final VacationRepository repository;


    private final MutableLiveData<String> vacationTitle = new MutableLiveData<>();
    private final MutableLiveData<String> vacationHotel = new MutableLiveData<>();

    public MutableLiveData<LocalDate> getVacationEndDate() {
        return vacationEndDate;
    }

    public MutableLiveData<LocalDate> getVacationStartDate() {
        return vacationStartDate;
    }

    private final MutableLiveData<LocalDate> vacationEndDate = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> vacationStartDate = new MutableLiveData<>();


    public int getCurrentVacationId() {
        return currentVacationId;
    }

    private int currentVacationId = 0;

    public VacationViews(@NonNull Application application) {
        super(application);
        repository = new VacationRepository(application);
    }

    public void setEditingVacation(Vacation vacation) {
        currentVacationId = Math.toIntExact(vacation.id);
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


    public void saveVacation() {
        Vacation vacation = new Vacation();
        vacation.title = vacationTitle.getValue();
        vacation.hotel = vacationHotel.getValue();



        if (currentVacationId == 0) {
            repository.insertVacation(vacation, newId -> {
                currentVacationId = Math.toIntExact(newId);
                Log.d("InsertedVacation", "Vacation saved with ID: " + newId);
            });
        } else {
            vacation.id = currentVacationId;
            repository.updateVacation(vacation);
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

    }
}
