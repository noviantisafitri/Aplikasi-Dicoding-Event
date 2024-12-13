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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventdicoding.R
import com.example.eventdicoding.databinding.FragmentEventBinding
import com.example.eventdicoding.databinding.LayoutErrorBinding
import com.example.eventdicoding.ui.adapter.EventAdapter
import com.example.eventdicoding.ui.model.EventViewModel

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventViewModel: EventViewModel
    private lateinit var adapter: EventAdapter
    private var eventType: Int = 1 // Default to upcoming events
    private var hasLoadedData = false

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var isNetworkCallbackRegistered = false

    companion object {
        private const val ARG_EVENT_TYPE = "eventType"

        fun newInstance(eventType: Int): EventFragment {
            val fragment = EventFragment()
            val args = Bundle().apply {
                putInt(ARG_EVENT_TYPE, eventType)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventViewModel = ViewModelProvider(this)[EventViewModel::class.java]

        eventType = arguments?.getInt(ARG_EVENT_TYPE) ?: 1

        setupRecyclerView()
        observeViewModel()

        if (!isNetworkCallbackRegistered) {
            startNetworkCallback()
        }

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
        // Observe regular events
        eventViewModel.events.observe(viewLifecycleOwner) { eventList ->
            adapter.submitList(eventList)
        }

        // Observe search results
        eventViewModel.searchResults.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults.isNotEmpty()) {
                adapter.submitList(searchResults)
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

    private fun setupRecyclerView() {
        adapter = EventAdapter()
        binding.rvEvent.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvent.adapter = adapter
        binding.rvEvent.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvEvent.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError() {
        val errorBinding = LayoutErrorBinding.bind(binding.includeErrorLayout.root)
        errorBinding.errorLayout.visibility = View.VISIBLE
        errorBinding.tvErrorMessage.text = getString(R.string.no_internet_connection)

        binding.progressBar.visibility = View.GONE
        binding.rvEvent.visibility = View.GONE

        errorBinding.btnRetry.setOnClickListener {
            errorBinding.errorLayout.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            loadData()
        }
    }

    private fun loadData() {
        if (hasLoadedData) return
        eventViewModel.loadEvents(eventType)
        hasLoadedData = true
    }

    fun search(query: String) {
        if (query.isEmpty()) {
            loadData()
        } else {
            eventViewModel.searchEvents(query)
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
