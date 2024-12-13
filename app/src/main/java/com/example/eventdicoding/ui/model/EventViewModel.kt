package com.example.eventdicoding.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventdicoding.data.response.EventResponse
import com.example.eventdicoding.data.response.ListEventsItem
import com.example.eventdicoding.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EventViewModel : ViewModel() {

    private val _events = MutableLiveData<List<ListEventsItem>>()
    val events: LiveData<List<ListEventsItem>> = _events

    private val _upcomingEvents = MutableLiveData<List<ListEventsItem>>()
    val upcomingEvents: LiveData<List<ListEventsItem>> = _upcomingEvents

    private val _finishedEvents = MutableLiveData<List<ListEventsItem>>()
    val finishedEvents: LiveData<List<ListEventsItem>> = _finishedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _searchResults = MutableLiveData<List<ListEventsItem>>()
    val searchResults: LiveData<List<ListEventsItem>> = _searchResults

    fun loadEvents(eventType: Int) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(eventType)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _events.value = response.body()?.listEvents?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load events: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failed to load events: ${t.message}"
            }
        })
    }

    fun loadUpcomingEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(1)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _upcomingEvents.value = response.body()?.listEvents?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load upcoming events: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failed to load upcoming events: ${t.message}"
            }
        })
    }

    fun loadFinishedEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getEvents(0)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _finishedEvents.value = response.body()?.listEvents?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to load finished events: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failed to load finished events: ${t.message}"
            }
        })
    }

    fun searchEvents(query: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().searchEvents(query)
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _searchResults.value = response.body()?.listEvents?.filterNotNull() ?: emptyList()
                } else {
                    _errorMessage.value = "Failed to search events: ${response.message()}"
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Failed to search events: ${t.message}"
            }
        })
    }

}
