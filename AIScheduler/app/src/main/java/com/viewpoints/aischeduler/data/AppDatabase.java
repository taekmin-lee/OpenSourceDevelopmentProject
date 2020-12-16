package com.viewpoints.aischeduler.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.viewpoints.aischeduler.data.model.Schedule;
import com.viewpoints.aischeduler.data.model.ScheduleDao;

@Database(entities = {Schedule.class}, version = 1, exportSchema = false)
@TypeConverters({DateTimeConverter.class, EnumConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ScheduleDao scheduleDao();

    private static volatile AppDatabase instance;

    public static AppDatabase getInstance(final Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "temp1234").allowMainThreadQueries().build();
                }
            }
        }

        return instance;
    }
}
