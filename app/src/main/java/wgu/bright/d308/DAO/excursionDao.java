package wgu.bright.d308.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import wgu.bright.d308.entities.Excursion;

@Dao
public interface excursionDao {
    @Insert
    long insert(Excursion excursion);

    @Update
    void update(Excursion excursion);

    @Delete
    void delete(Excursion excursion);

    @Query("SELECT * FROM excursions WHERE vacationId = :vacationId")
    List<Excursion> getExcursionsForVacation(long vacationId);
    @Query("SELECT * FROM excursions WHERE Id = :excursionId")
    Excursion getExcursionById(long excursionId);
}

