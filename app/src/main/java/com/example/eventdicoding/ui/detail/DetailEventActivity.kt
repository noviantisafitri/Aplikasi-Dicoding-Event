package com.example.eventdicoding.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.example.eventdicoding.databinding.ActivityDetailBinding
import androidx.core.text.HtmlCompat
import com.example.eventdicoding.R
import com.example.eventdicoding.data.database.local.entity.DetailDataEventEntity
import com.example.eventdicoding.data.database.local.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.ContextCompat
import com.example.eventdicoding.ui.model.FavoriteEventViewModel

class DetailEventActivity : AppCompatActivity() {

    private val eventViewModel: FavoriteEventViewModel by viewModels()
    private lateinit var binding: ActivityDetailBinding
    private val detailEventViewModel: DetailEventViewModel by viewModels()
    private val eventRepository: EventRepository by lazy { EventRepository(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val upArrow = ResourcesCompat.getDrawable(resources, androidx.appcompat.R.drawable.abc_ic_ab_back_material, null)
        val wrappedDrawable = upArrow?.let { DrawableCompat.wrap(it) }

        wrappedDrawable?.let { DrawableCompat.setTint(it, ContextCompat.getColor(this, android.R.color.white)) }
        supportActionBar?.setHomeAsUpIndicator(wrappedDrawable)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val eventId = intent.getIntExtra("id", 0)
        if (eventId == 0) {
            Toast.makeText(this, "Event ID tidak valid", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        detailEventViewModel.isLoading.observe(this) { isLoading -> showLoading(isLoading) }

        detailEventViewModel.eventDetail.observe(this) { detailEventResponse ->
            detailEventResponse?.event?.let { event ->
                binding.tvEventName.text = event.name ?: "Nama"
                supportActionBar?.title = event.name ?: "Event Detail"
                binding.tvDescription.text = HtmlCompat.fromHtml(event.description ?: "No Description", HtmlCompat.FROM_HTML_MODE_LEGACY)
                binding.tvOwnerName.text = event.ownerName ?: "Owner"
                binding.tvBeginTime.text = event.beginTime ?: "Waktu"

                val quota = event.quota ?: 0
                val registrants = event.registrants
                val availableQuota = quota - registrants

                binding.tvQuota.text = when {
                    quota == 0 -> "Kuota tidak tersedia"
                    availableQuota > 0 -> "Sisa Kuota: $availableQuota"
                    else -> "Kuota Penuh"
                }

                Glide.with(this).load(event.mediaCover).into(binding.ivEventImage)

                if (!event.link.isNullOrEmpty()) {
                    binding.btnOpenLink.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                        startActivity(intent)
                    }
                } else {
                    binding.btnOpenLink.isEnabled = false
                }

                checkIfEventIsFavorite(event.id)

                binding.includeErrorLayout.root.visibility = View.GONE
                binding.contentLayout.visibility = View.VISIBLE
            }
        }

        binding.btnFavorite.setOnClickListener {
            val eventDetail = detailEventViewModel.eventDetail.value?.event
            eventDetail?.let { event ->
                val eventEntity = eventRepository.mapRemoteToLocal(event)

                CoroutineScope(Dispatchers.IO).launch {
                    val isFavorite = isEventInFavorites(event.id)
                    withContext(Dispatchers.Main) {
                        if (isFavorite) {
                            deleteEventFromDatabase(eventEntity)
                        } else {
                            insertEventToDatabase(eventEntity)
                        }
                    }
                }
            }
        }

        loadData(eventId)
    }

    private fun checkIfEventIsFavorite(eventId: Int?) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                eventId?.let {
                    val event = eventRepository.getEventById(eventId)
                    withContext(Dispatchers.Main) {
                        if (event != null) {
                            binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24)
                        } else {
                            binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("DetailEventActivity", "Error checking favorite: ${e.message}")
            }
        }
    }

    private fun insertEventToDatabase(event: DetailDataEventEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                eventRepository.insert(event)

                val eventId = event.id ?: return@launch

                val insertedEvent = eventRepository.getEventById(eventId)

                withContext(Dispatchers.Main) {
                    if (insertedEvent != null) {
                        Toast.makeText(this@DetailEventActivity, "Event Add To Favorite", Toast.LENGTH_SHORT).show()
                        binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_24)

                        eventViewModel.refreshFavoriteEvents()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("DetailEventActivity", "Error inserting event: ${e.message}")
                    Toast.makeText(this@DetailEventActivity, "Error saving event", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteEventFromDatabase(event: DetailDataEventEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                eventRepository.delete(event)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailEventActivity, "Event removed from favorites", Toast.LENGTH_SHORT).show()

                    binding.btnFavorite.setImageResource(R.drawable.baseline_favorite_border_24)

                    eventViewModel.refreshFavoriteEvents()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("DetailEventActivity", "Error deleting event: ${e.message}")
                    Toast.makeText(this@DetailEventActivity, "Error removing event", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private suspend fun isEventInFavorites(eventId: Int?): Boolean {
        return eventId?.let {
            val event = eventRepository.getEventById(it)
            event != null
        } ?: false
    }

    private fun loadData(eventId: Int) {
        detailEventViewModel.getDetailEvent(eventId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.contentLayout.visibility = View.GONE
            binding.includeErrorLayout.root.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }
}
