package com.ilatyphi95.farmersmarket.ui.ads

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.entities.Product
import com.ilatyphi95.farmersmarket.data.repository.SampleRepository
import com.ilatyphi95.farmersmarket.databinding.FragmentPagerBinding
import com.ilatyphi95.farmersmarket.utils.EventObserver
import com.ilatyphi95.farmersmarket.utils.LocationUtils


class PagerFragment : Fragment() {
    private lateinit var binding: FragmentPagerBinding

    private val viewmodel by viewModels<AdsFragmentViewModel> {
        AdsFragmentViewModelFactory(SampleRepository())
    }

    private val handleLocation = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            findNavController().navigate(
                PagerFragmentDirections.actionNavigationPagerToAddProductFragment(NEW_PRODUCT)
            )
        } else {
            Snackbar.make(
                requireView(), getString(R.string.add_new_product_require_location),
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPagerBinding.inflate(inflater, container, false)
        viewmodel.apply {
            eventAdsDetails.observe(viewLifecycleOwner, EventObserver {
                findNavController()
                    .navigate(PagerFragmentDirections.actionNavigationPagerToProductFragment(it))
            })

            eventEditAds.observe(viewLifecycleOwner, EventObserver { adId ->
                when {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {

                        LocationUtils.checkLocationRequest(requireActivity()) {
                            findNavController().navigate(
                                PagerFragmentDirections.actionNavigationPagerToAddProductFragment(
                                    adId
                                )
                            )
                        }
                    }

                    shouldShowRequestPermissionRationale(
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) -> {

                        Snackbar.make(
                            requireView(), getString(R.string.add_new_product_require_location),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        handleLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }
            })
        }

        setUpListeners()

        return binding.root
    }

    private fun setUpListeners() {
        val thisUser = FirebaseAuth.getInstance().uid
        val firestoreRef = FirebaseFirestore.getInstance()

        firestoreRef.collection("ads").whereEqualTo("sellerId", thisUser)
            .addSnapshotListener(requireActivity()) { value, error ->
            if(error != null) {
                return@addSnapshotListener
            }

            value?.toObjects<Product>()?.let { viewmodel.upDatePostedAds(it) }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentArray = arrayListOf(PostedAdsFragment(), InterestedAdsFragment())

        val pager = binding.pager.apply {
            adapter = AdsPagerAdapter(this@PagerFragment, fragmentArray)
        }
        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, pager) { tab, position ->
            if (position == 0) {
                tab.text = getString(R.string.my_ads)
            } else {
                tab.text = getString(R.string.interested_ads)
            }
        }.attach()
    }
}