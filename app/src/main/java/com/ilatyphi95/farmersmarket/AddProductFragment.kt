package com.ilatyphi95.farmersmarket

import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ilatyphi95.farmersmarket.databinding.FragmentAddProductBinding
import com.ilatyphi95.farmersmarket.utils.EventObserver
import com.ilatyphi95.farmersmarket.utils.LocationProvider
import com.ilatyphi95.farmersmarket.utils.SampleRepository

/**
 * A simple [Fragment] subclass.
 * Use the [AddProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddProductFragment : Fragment() {
    private lateinit var binding: FragmentAddProductBinding
    private val viewmodel by viewModels<AddProductViewModel> {
        AddProductViewModelFactory(SampleRepository())
    }

    private val handlePictures = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { imageList ->
        viewmodel.addImages(imageList)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_product, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewmodel
        }

        LocationProvider(requireActivity()) { isSucessful, address ->
            if (isSucessful) {
                viewmodel.updateAddress(address)
            }
            //handle failure case

        }

        viewmodel.apply {
            events.observe(viewLifecycleOwner, EventObserver {

                finishLoading()

                when (it) {
                    Loads.ADD_PICTURES -> handlePictures.launch("image/*")

                    Loads.LOAD_CURRENCY -> (setupSpinnerDialog(R.string.search_currency)
                    { position -> viewmodel.updateCurrency(position) }).showSpinerDialog()

                    Loads.LOAD_CATEGORY -> (setupSpinnerDialog(R.string.search_currency)
                    { position -> viewmodel.selectCategory(position) }).showSpinerDialog()
                }
            })


            isLoading.observe(viewLifecycleOwner, {
                it
            })


        }
        return binding.root
    }

    private fun setupSpinnerDialog(
        @StringRes searchName: Int, @StringRes noList: Int = R.string.no_items,
        usePosition: (Int) -> Unit
    ): SpinnerDialog {
        val myList = viewmodel.loadedList ?: listOf(getString(noList))
        val spinnerDialog = SpinnerDialog(activity, ArrayList(myList), getString(searchName))
        spinnerDialog.setCancellable(true)
        spinnerDialog.setShowKeyboard(false)

        spinnerDialog.bindOnSpinerListener { _, position ->
            usePosition(position)
        }
        return spinnerDialog
    }
}
