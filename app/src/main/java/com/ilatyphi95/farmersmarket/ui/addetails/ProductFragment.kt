package com.ilatyphi95.farmersmarket.ui.addetails

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.PagerSnapHelper
import com.ilatyphi95.farmersmarket.data.universaladapter.OnSnapPositionChangeListener
import com.ilatyphi95.farmersmarket.databinding.FragmentProductBinding
import com.ilatyphi95.farmersmarket.utils.EventObserver
import com.ilatyphi95.farmersmarket.data.repository.SampleRepository
import com.ilatyphi95.farmersmarket.data.universaladapter.attachSnapHelperWithListener

/**
 * ProductFragment to use to show product details
 */
class ProductFragment : Fragment() {
    private val args: ProductFragmentArgs by navArgs()

    private val viewModel by viewModels<ProductViewModel> {
        ProductViewModelFactory(args.product, SampleRepository())
    }

    private lateinit var databinding: FragmentProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        databinding = FragmentProductBinding.inflate(inflater, container, false)
        databinding.apply {
            viewmodel = viewModel
            val snapHelper = PagerSnapHelper()
            rvImages.attachSnapHelperWithListener(snapHelper, onSnapPositionChangeListener = object :
                OnSnapPositionChangeListener {
                override fun onSnapPositionChange(position: Int) {
                    viewModel.pictureSelected(position)
                }
            })

        }

        viewModel.apply {
            eventProductSelected.observe(viewLifecycleOwner, EventObserver{
                findNavController().navigate(ProductFragmentDirections.actionProductFragmentSelf(it))
            })
        }

        databinding.lifecycleOwner = this

        return databinding.root
    }
}