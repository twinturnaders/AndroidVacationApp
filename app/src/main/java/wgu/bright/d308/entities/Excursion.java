package wgu.bright.d308.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "excursions",
        foreignKeys = @ForeignKey(
                entity = Vacation.class,
                parentColumns = "id",
                childColumns = "vacationId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("vacationId")}
)
public class Excursion {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getVacationId() {
        return vacationId;
    }

    public void setVacationId(long vacationId) {
        this.vacationId = vacationId;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public long vacationId;
    @NonNull
    public String title;

    @NonNull
    public String date;

    public Excursion(long id, long vacationId, @NonNull String title, @NonNull String date) {
        this.id = id;
        this.vacationId = vacationId;
        this.title = title;
        this.date = date;
    }


    public Excursion() {

    }
}

