package wgu.bright.d308.Views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;

import wgu.bright.d308.VacationRepository;
import wgu.bright.d308.entities.Excursion;

public class ExcursionViews extends AndroidViewModel {

    private final VacationRepository repository;

    private final MutableLiveData<String> excursionTitle = new MutableLiveData<>();
    private final MutableLiveData<String> excursionDate = new MutableLiveData<>();
    private long currentExcursionId = 0;
    private long vacationId = 0;

    public ExcursionViews(@NonNull Application application) {
        super(application);
        repository = new VacationRepository(application);
    }

    public void setEditingExcursion(Excursion excursion) {
        currentExcursionId = excursion.id;
        vacationId = excursion.vacationId;
        excursionTitle.setValue(excursion.title);
        excursionDate.setValue(excursion.date);
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
        Excursion excursion = new Excursion();
        excursion.title = Objects.requireNonNull(excursionTitle.getValue());
        excursion.date = Objects.requireNonNull(excursionDate.getValue());
        excursion.vacationId = vacationId;

        if (currentExcursionId == 0) {
            repository.getExcursionDao().insert(excursion);
        } else {
            excursion.id = currentExcursionId;
            repository.getExcursionDao().update(excursion);
        }

        onSaved.accept(currentExcursionId);
    }

    public void deleteExcursion() {
        if (currentExcursionId == 0) return;
        Excursion excursion = new Excursion();
        excursion.id = currentExcursionId;
        excursion.title = Objects.requireNonNull(excursionTitle.getValue());
        excursion.date = Objects.requireNonNull(excursionDate.getValue());
        excursion.vacationId = vacationId;
        repository.getExcursionDao().delete(excursion);
    }

    public long getExcursionId() {
        return currentExcursionId;
    }
}
