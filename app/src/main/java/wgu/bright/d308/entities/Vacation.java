package wgu.bright.d308.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
//b2
@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String phone;
    public String title;
    public String hotel;

    public String startDate;
    public String endDate;

    Vacation(long id, String phone, String title, String hotel, String startDate, String endDate){
        this.id = id;
        this.title = title;
        this.hotel = hotel;
        this.startDate = startDate;
        this.endDate = endDate;
        this.phone = phone;
    }

    public Vacation(){

    }

}
