package com.ilatyphi95.farmersmarket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ilatyphi95.farmersmarket.databinding.FragmentHomeBinding
import com.ilatyphi95.farmersmarket.utils.EventObserver
import com.ilatyphi95.farmersmarket.data.repository.SampleRepository

class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding

    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(SampleRepository())
    }

    private val queryTextListener: OnQueryTextListener = object : OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            homeViewModel.search(query)
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            // implement this in the future
            return true
        }
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
            searchView.setOnQueryTextListener(queryTextListener)
        }

        homeViewModel.apply {
            eventProductSelected.observe(viewLifecycleOwner, EventObserver {
                findNavController()
                    .navigate(HomeFragmentDirections.actionNavigationHomeToProductFragment(it))
            })

            isLoading.observe(viewLifecycleOwner, {
            })
        }

        return binding.root
    }
}