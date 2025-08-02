package wgu.bright.d308;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

import android.app.Application;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.security.auth.callback.Callback;

import wgu.bright.d308.DAO.VacationDao;
import wgu.bright.d308.DAO.excursionDao;
import wgu.bright.d308.data.AppData;
import wgu.bright.d308.entities.Excursion;
import wgu.bright.d308.entities.Vacation;

public class VacationRepository {

    private final VacationDao vacationDao;
    private final excursionDao excursionDao;
    private final Executor executor;

    public VacationRepository(Application application) {
        AppData db = AppData.getDatabase(application);
        vacationDao = db.vacationDao();
        excursionDao = db.excursionDao();
        executor = newSingleThreadExecutor();


    }

    public void insertVacation(Vacation vacation, Consumer<Long> callback) {
        executor.execute(() -> {
            long id = vacationDao.insert(vacation);
            callback.accept(id);
        });
    }

    public void updateVacation(Vacation vacation) {
        executor.execute(() -> vacationDao.update(vacation));
    }

    public void deleteVacation(Vacation vacation) {
        executor.execute(() -> vacationDao.delete(vacation));
    }

    public void findVacation(Vacation vacation) {
        executor.execute(() -> vacationDao.getVacationByTitle(vacation.title));
    }

    //check repo for excursions outside of main thread
    public void canDelete(Vacation vacation, Consumer<Boolean> callback) {
        executor.execute(() -> {
            List<Excursion> excursions = excursionDao.getExcursionsForVacation(vacation.id);
            boolean canDelete = excursions.isEmpty();
            callback.accept(canDelete);
        });


    }

    public excursionDao getExcursionDao() {
        return excursionDao;
    }
}
