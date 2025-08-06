package wgu.bright.d308.Views;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import android.app.Application;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import wgu.bright.d308.VacationRepository;
import wgu.bright.d308.entities.Excursion;

public class ExcursionViews extends AndroidViewModel {

    private final VacationRepository repository;

    private final MutableLiveData<String> excursionTitle = new MutableLiveData<>();
    private final MutableLiveData<LocalDate> excursionDate = new MutableLiveData<>();
    private Excursion excursion;


    public long getExcursionId() {
        return ExcursionId;
    }

    public long getCurrentExcursionId() {
        return currentExcursionId;
    }

    public void setCurrentExcursionId(long currentExcursionId) {
        this.currentExcursionId = currentExcursionId;
    }

    private long ExcursionId;
    private long currentExcursionId;
    private long vacationId;

    public ExcursionViews(@NonNull Application application) {
        super(application);
        repository = new VacationRepository(application);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setEditingExcursion(Excursion excursion) {
        currentExcursionId = excursion.id;
        vacationId = excursion.vacationId;
        excursionTitle.setValue(excursion.title);
        excursionDate.setValue(LocalDate.parse(excursion.date));
    }




    public MutableLiveData<String> getExcursionTitle() {
        return excursionTitle;
    }

    public MutableLiveData<LocalDate> getExcursionDate() {
        return excursionDate;
    }

    public long getVacationId() {
        return vacationId;
    }

    public void setVacationId(long id) {
        vacationId = id;
    }

    public void saveExcursion(Consumer<Long> onSaved) {
        //use executor so we don't save on main thread and crash
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Excursion excursion = new Excursion();
            excursion.title = Objects.requireNonNull(excursionTitle.getValue());
            excursion.date = String.valueOf(Objects.requireNonNull(excursionDate.getValue()));
            excursion.vacationId = vacationId;

            if (currentExcursionId == 0) {
                long newId = repository.getExcursionDao().insert(excursion);
                currentExcursionId = newId;
                excursion.id = newId;
                setExcursionId(newId);

            }
            else {
                excursion.id = currentExcursionId;
                repository.getExcursionDao().update(excursion);
            }


            new Handler(Looper.getMainLooper()).post(() -> onSaved.accept(currentExcursionId));
        });
    }






    public void deleteExcursion() {
        if (currentExcursionId == 0) return;
        Excursion excursion = new Excursion();
        excursion.id = currentExcursionId;
        excursion.title = Objects.requireNonNull(excursionTitle.getValue());
        excursion.date = String.valueOf(Objects.requireNonNull(excursionDate.getValue()));
        excursion.vacationId = vacationId;
        repository.getExcursionDao().delete(excursion);
    }

    public Excursion getExcursionById(long excursionId){
        return excursion;
    }

    public void setExcursionId(long excursionId) {
        ExcursionId = excursionId;
    }

    public VacationRepository getRepository() {
        return repository;
    }
}


