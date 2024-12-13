package com.example.eventdicoding.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.R
import com.example.eventdicoding.databinding.FragmentFavoriteBinding
import com.example.eventdicoding.ui.adapter.FavoriteEventAdapter
import com.example.eventdicoding.ui.model.FavoriteEventViewModel

class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private lateinit var eventViewModel: FavoriteEventViewModel
    private lateinit var eventAdapter: FavoriteEventAdapter
    private lateinit var binding: FragmentFavoriteBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)

        eventViewModel = viewModels<FavoriteEventViewModel>().value
        eventAdapter = FavoriteEventAdapter()

        binding.rvEvent.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = eventAdapter
        }

        eventViewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }

        eventViewModel.refreshFavoriteEvents()

        eventViewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            if (events != null && events.isNotEmpty()) {
                eventAdapter.submitList(events)
                showError(false)
            } else {
                showError(true)
            }
        }
    }

    private fun showError(show: Boolean) {
        if (show) {
            binding.errorLayout.visibility = View.VISIBLE
            binding.errorMessage.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.errorLayout.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }
    }
}

