package com.example.eventdicoding.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eventdicoding.R
import com.example.eventdicoding.databinding.FragmentHomeBinding
import com.example.eventdicoding.databinding.LayoutErrorBinding
import com.example.eventdicoding.ui.adapter.EventAdapter
import com.example.eventdicoding.ui.model.EventViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val eventViewModel: EventViewModel by viewModels()

    private lateinit var upcomingAdapter: EventAdapter
    private lateinit var finishedAdapter: EventAdapter
    private lateinit var searchAdapter: EventAdapter
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isNetworkCallbackRegistered = false
    private var hasLoadedData = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()

        if (!isNetworkCallbackRegistered) {
            startNetworkCallback()
        }

        observeViewModel()

        if (!hasLoadedData) {
            loadData()
        }
    }

    private fun startNetworkCallback() {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                requireActivity().runOnUiThread {
                    if (!hasLoadedData) {
                        loadData()
                    }
                }
            }
        }

        val request = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback!!)
        isNetworkCallbackRegistered = true
    }

    private fun observeViewModel() {
        eventViewModel.upcomingEvents.observe(viewLifecycleOwner) { upcomingEvents ->
            val limitedEvents = upcomingEvents.take(5)
            upcomingAdapter.submitList(limitedEvents)
        }

        eventViewModel.finishedEvents.observe(viewLifecycleOwner) { finishedEvents ->
            val limitedEvents = finishedEvents.take(5)
            finishedAdapter.submitList(limitedEvents)
        }

        eventViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults.isNotEmpty()) {
                binding.rvUpcoming.visibility = View.GONE
                binding.tvUpcoming.visibility = View.GONE
                binding.rvFinished.visibility = View.GONE
                binding.tvFinished.visibility = View.GONE

                binding.tvSearchResults.visibility = View.VISIBLE
                binding.rvSearchResults.visibility = View.VISIBLE

                searchAdapter.submitList(searchResults)
            }
        }

        eventViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }

        eventViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                showError()
            }
        }
    }

    private fun setupRecyclerViews() {
        upcomingAdapter = EventAdapter()
        binding.rvUpcoming.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvUpcoming.adapter = upcomingAdapter
        binding.rvUpcoming.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.HORIZONTAL))

        finishedAdapter = EventAdapter()
        binding.rvFinished.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvFinished.adapter = finishedAdapter
        binding.rvFinished.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

        searchAdapter = EventAdapter()
        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        binding.rvSearchResults.adapter = searchAdapter
        binding.rvSearchResults.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError() {
        val errorBinding = LayoutErrorBinding.bind(binding.includeErrorLayout.root)
        errorBinding.errorLayout.visibility = View.VISIBLE
        errorBinding.tvErrorMessage.text = getString(R.string.no_internet_connection)
        binding.progressBar.visibility = View.GONE
        binding.contentLayout.visibility = View.GONE

        errorBinding.btnRetry.setOnClickListener {
            errorBinding.errorLayout.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            loadData()
        }
    }


    fun search(query: String) {
        eventViewModel.searchEvents(query)
    }

    private fun loadData() {
        if (!hasLoadedData) {
            eventViewModel.loadUpcomingEvents()
            eventViewModel.loadFinishedEvents()
            hasLoadedData = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isNetworkCallbackRegistered && networkCallback != null) {
            val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallback!!)
            isNetworkCallbackRegistered = false
        }
    }
}