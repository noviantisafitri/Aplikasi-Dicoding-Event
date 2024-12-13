package com.example.eventdicoding.data.database.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.eventdicoding.data.database.local.entity.DetailDataEventEntity

@Dao
interface EventDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(detail: DetailDataEventEntity)

    @Delete
    suspend fun delete(detail: DetailDataEventEntity)

    @Query("SELECT * FROM detail_data_event")
    fun getAllEvents(): LiveData<List<DetailDataEventEntity>>

    @Query("SELECT * FROM detail_data_event WHERE id = :eventId LIMIT 1")
    suspend fun getEventById(eventId: Int): DetailDataEventEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM detail_data_event WHERE id = :eventId)")
    fun isEventFavorite(eventId: Int): LiveData<Boolean>
}
