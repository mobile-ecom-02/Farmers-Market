package com.ilatyphi95.farmersmarket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.ilatyphi95.farmersmarket.databinding.FragmentPagerBinding
import com.ilatyphi95.farmersmarket.utils.EventObserver
import com.ilatyphi95.farmersmarket.utils.LocationProvider
import com.ilatyphi95.farmersmarket.utils.SampleRepository


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
                PagerFragmentDirections
                    .actionNavigationPagerToAddProductFragment(NEW_PRODUCT)
            )
        } else {
            Snackbar.make(requireView(), getString(R.string.add_new_product_require_location),
                Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

            eventEditAds.observe(viewLifecycleOwner, EventObserver {
                if (it == NEW_PRODUCT) {
                    if (LocationProvider.isLocationEnabled(requireContext())) {
                        findNavController().navigate(
                            PagerFragmentDirections
                                .actionNavigationPagerToAddProductFragment(it)
                        )
                    } else {
                        handleLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }

                } else {
                    findNavController()
                        .navigate(
                            PagerFragmentDirections.actionNavigationPagerToAddProductFragment(
                                it
                            )
                        )
                }
            })
        }
        return binding.root
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