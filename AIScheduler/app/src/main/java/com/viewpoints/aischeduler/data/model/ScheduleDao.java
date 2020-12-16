package com.viewpoints.aischeduler.data.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ScheduleDao {
    @Query("SELECT * FROM 'schedule' ORDER BY 'start', 'end'")
    LiveData<List<Schedule>> getAll();

    @Query("SELECT * FROM 'schedule' WHERE rowid = :id")
    Schedule get(int id);

    @Insert
    void insert(Schedule schedule);

    @Update
    void Update(Schedule schedule);

    @Delete
    void delete(Schedule schedule);
}
