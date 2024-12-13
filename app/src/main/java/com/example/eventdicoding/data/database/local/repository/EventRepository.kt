package com.example.eventdicoding.data.database.local.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.eventdicoding.data.database.local.entity.DetailDataEventEntity
import com.example.eventdicoding.data.database.local.room.EventDAO
import com.example.eventdicoding.data.database.local.room.EventRoomDatabase
import com.example.eventdicoding.data.response.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EventRepository(application: Application) {
    private val mEventDao: EventDAO

    init {
        val db = EventRoomDatabase.getDatabase(application)
        mEventDao = db.eventDAO()
    }

    suspend fun insert(detailData: DetailDataEventEntity) {
        withContext(Dispatchers.IO) {
            mEventDao.insert(detailData)
        }
    }

    suspend fun getEventById(eventId: Int): DetailDataEventEntity? {
        return mEventDao.getEventById(eventId)
    }

    suspend fun delete(detailData: DetailDataEventEntity) {
        withContext(Dispatchers.IO) {
            mEventDao.delete(detailData)
        }
    }

    fun getAllFavoriteEvents(): LiveData<List<DetailDataEventEntity>> = mEventDao.getAllEvents()

    fun mapRemoteToLocal(detailData: Event): DetailDataEventEntity {
        return DetailDataEventEntity(
            id = detailData.id ?: 0,
            name = detailData.name,
            ownerName = detailData.ownerName,
            summary = detailData.summary,
            description = detailData.description,
            imageLogo = detailData.imageLogo,
            category = detailData.category,
            cityName = detailData.cityName,
            mediaCover = detailData.mediaCover,
            beginTime = detailData.beginTime,
            registrants = detailData.registrants,
            endTime = detailData.endTime,
            link = detailData.link
        )
    }
}
