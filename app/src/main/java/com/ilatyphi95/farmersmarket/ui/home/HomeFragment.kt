package com.ilatyphi95.farmersmarket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ilatyphi95.farmersmarket.data.ProductAdapter
import com.ilatyphi95.farmersmarket.databinding.FragmentHomeBinding
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.EventObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding
    private val productSearchAdapter = ProductAdapter()

    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(ProductServices)
    }

    private val backPressedCallback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(homeViewModel.showSearchRecycler.value == true) {
                homeViewModel.closeSearchView()
            }
        }
    }

    private val queryTextListener: OnQueryTextListener = object : OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            query?.let {
                lifecycleScope.launch {
                    homeViewModel.productFlow(it).collectLatest {
                        productSearchAdapter.submitData(it)
                    }
                }
                homeViewModel.openSearchView()
            }
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
    ): View {

        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
            rvProductSearch.adapter = productSearchAdapter
            searchView.setOnQueryTextListener(queryTextListener)
        }

        homeViewModel.apply {
            eventProductSelected.observe(viewLifecycleOwner, EventObserver {
                findNavController()
                    .navigate(HomeFragmentDirections.actionNavigationHomeToProductFragment(it))
            })

            isLoading.observe(viewLifecycleOwner, {
            })

            showSearchRecycler.observe(viewLifecycleOwner) {isShown ->
                backPressedCallback.isEnabled = isShown
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
    }

    override fun onPause() {
        backPressedCallback.remove()
        super.onPause()
    }
}