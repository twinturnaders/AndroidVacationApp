package wgu.bright.d308.DAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import wgu.bright.d308.entities.Vacation;

@Dao
public interface VacationDao {
    @Insert
    long insert(Vacation vacation);

    @Update
    void update(Vacation vacation);

    @Delete
    void delete(Vacation vacation);

    @Query("SELECT * FROM vacations WHERE id = :id")
    Vacation getVacationById(long id);

    @Query("SELECT * FROM vacations WHERE title = :title")
    Vacation getVacationByTitle(String title);

    @Query("SELECT * FROM vacations")
    List<Vacation> getAllVacations();


   @Query("SELECT * FROM vacations WHERE phone = :phone")
    List<Vacation> getVacationsByPhone(String phone);



}
