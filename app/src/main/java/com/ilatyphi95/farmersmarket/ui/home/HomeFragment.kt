package com.ilatyphi95.farmersmarket.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.entities.User
import com.ilatyphi95.farmersmarket.data.repository.SampleRepository
import com.ilatyphi95.farmersmarket.databinding.FragmentHomeBinding
import com.ilatyphi95.farmersmarket.firebase.addSnapshotListener
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.EventObserver
import com.koalap.geofirestore.GeoFire
import com.koalap.geofirestore.GeoLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HomeFragment : Fragment() {

    lateinit var binding : FragmentHomeBinding

    private val firestore = FirebaseFirestore.getInstance()

    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(ProductServices)
    }

    private val queryTextListener: OnQueryTextListener = object : OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            homeViewModel.search(query)
            homeViewModel.openSearchView()
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
            searchView.setOnQueryTextListener(queryTextListener)
            searchView.setOnCloseListener {
                SearchView.OnCloseListener {
                    homeViewModel.closeSearchView()
                    true
                }
                true
            }
        }

        homeViewModel.apply {
            eventProductSelected.observe(viewLifecycleOwner, EventObserver {
                findNavController()
                    .navigate(HomeFragmentDirections.actionNavigationHomeToProductFragment(it))
            })

            isLoading.observe(viewLifecycleOwner, {
            })
        }

        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
//        firestore.document("users/${FirebaseAuth.getInstance().currentUser?.uid}")
//            .collection("recent").addSnapshotListener(viewLifecycleOwner) { query, exception ->
//                if (exception != null) {
//                    Log.d(tag, "setUpFirestoreListeners: ${exception.message}")
//                }
//
//                query?.let {
//                    homeViewModel.updateRecent(it.toObjects())
//                }
//            }

//        firestore.collection("users")
//            .document("${FirebaseAuth.getInstance().currentUser?.uid}").get()
//            .addOnSuccessListener {
//
//                val user = it.toObject<User>()
//                user?.location?.let { myLocation ->
//                    val ref = firestore.collection("ads")
//
//                    GeoFire(ref, ref.limit(30))
//                        .queryAtLocation(
//                            GeoLocation(user.location.latitude, user.location.longitude), 100.0)
//                        .addGeoQueryForSingleValueEvent { list ->
//                            val  productList = list.map { docChange ->
//                                docChange.document.toObject<Product>()
//                            }
//                            homeViewModel.updateCloseBy(myLocation, productList)
//                        }
//                }
//            }
    }
}