package com.example.eventdicoding.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventdicoding.data.response.DetailEventResponse
import com.example.eventdicoding.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel : ViewModel() {

    private val _eventDetail = MutableLiveData<DetailEventResponse>()
    val eventDetail: LiveData<DetailEventResponse> = _eventDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()

    fun getDetailEvent(id: Int) {
        if (_eventDetail.value != null) {
            return
        }

        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(id)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                if (response.isSuccessful) {
                    _eventDetail.value = response.body()
                    Log.d("DetailEventViewModel", "Event Detail: ${response.body()}")
                } else {
                    _errorMessage.value = "Error ${response.code()}: ${response.message()}"
                    Log.e("DetailEventViewModel", "Response Error: ${response.code()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _errorMessage.value = "Failure: ${t.message}"
                _isLoading.value = false
                Log.e("DetailEventViewModel", "Failure: ${t.message}")
            }
        })
    }
}
