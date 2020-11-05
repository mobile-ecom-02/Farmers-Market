package com.ilatyphi95.farmersmarket.ui.ads

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.databinding.FragmentPostedAdsBinding


/**
 */
class PostedAdsFragment : Fragment() {

    private val viewmodel by viewModels<AdsFragmentViewModel>(ownerProducer = {
        requireParentFragment()
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentPostedAdsBinding>(
            inflater, R.layout.fragment_posted_ads, container, false)

        binding.apply {
            viewModel = viewmodel
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }
}