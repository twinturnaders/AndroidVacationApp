package wgu.bright.d308.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import wgu.bright.d308.DAO.VacationDao;
import wgu.bright.d308.DAO.excursionDao;
import wgu.bright.d308.entities.Excursion;
import wgu.bright.d308.entities.Vacation;

@Database(entities = {Vacation.class, Excursion.class}, version = 1)
public abstract class AppData extends RoomDatabase {

    private static volatile AppData INSTANCE;

    public abstract VacationDao vacationDao();
    public abstract excursionDao excursionDao();

    public static AppData getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppData.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppData.class, "vacation_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

