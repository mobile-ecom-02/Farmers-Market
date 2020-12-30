package com.ilatyphi95.farmersmarket.ui.addetails

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.PagerSnapHelper
import com.ilatyphi95.farmersmarket.data.universaladapter.OnSnapPositionChangeListener
import com.ilatyphi95.farmersmarket.data.universaladapter.attachSnapHelperWithListener
import com.ilatyphi95.farmersmarket.databinding.FragmentProductBinding
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.EventObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * ProductFragment to use to show product details
 */
@ExperimentalCoroutinesApi
class ProductFragment : Fragment() {
    private val args: ProductFragmentArgs by navArgs()

    private val viewModel by viewModels<ProductViewModel> {
        ProductViewModelFactory(args.product, ProductServices)
    }

    private lateinit var databinding: FragmentProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        databinding = FragmentProductBinding.inflate(inflater, container, false)

        setupRecyclerView()

        addEventListeners()

        databinding.lifecycleOwner = this

        return databinding.root
    }

    private fun setupRecyclerView() {
        databinding.apply {
            viewmodel = viewModel
            val snapHelper = PagerSnapHelper()
            rvImages.attachSnapHelperWithListener(
                snapHelper,
                onSnapPositionChangeListener = object :
                    OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        viewModel.pictureSelected(position)
                    }
                })

        }
    }

    private fun addEventListeners() {
        viewModel.apply {
            eventProductSelected.observe(viewLifecycleOwner, EventObserver {
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentSelf(it))
            })

            eventMessage.observe(viewLifecycleOwner, EventObserver {
                findNavController()
                    .navigate(ProductFragmentDirections.actionProductFragmentToChatFragment(it))
            })

            eventCall.observe(viewLifecycleOwner, EventObserver { number ->

                if (number.isDigitsOnly()) {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:$number")
                    }

                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {

                        Log.e(tag, "No Phone Activity")
                    }
                }
            })
        }
    }
}