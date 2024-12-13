package com.example.eventdicoding.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.eventdicoding.data.database.local.entity.DetailDataEventEntity
import com.example.eventdicoding.data.database.local.repository.EventRepository

class FavoriteEventViewModel(application: Application) : AndroidViewModel(application) {

    private val eventRepository: EventRepository = EventRepository(application)

    private val _favoriteEvents = MutableLiveData<List<DetailDataEventEntity>>()
    val favoriteEvents: LiveData<List<DetailDataEventEntity>> get() = _favoriteEvents

    private fun getAllFavoriteEvents() {
        eventRepository.getAllFavoriteEvents().observeForever { events ->
            _favoriteEvents.postValue(events)
        }
    }

    fun refreshFavoriteEvents() {
        getAllFavoriteEvents()
    }
}
