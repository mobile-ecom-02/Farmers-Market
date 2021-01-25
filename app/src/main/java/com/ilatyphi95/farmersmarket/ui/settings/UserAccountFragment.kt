package com.ilatyphi95.farmersmarket.ui.settings

import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.material.snackbar.Snackbar
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.entities.MyLocation
import com.ilatyphi95.farmersmarket.databinding.UserAccountFragmentBinding
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import com.ilatyphi95.farmersmarket.utils.EventObserver
import com.ilatyphi95.farmersmarket.utils.LocationUtils
import com.ilatyphi95.farmersmarket.utils.NetworkAvailabilityUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.IOException
import java.util.*

@ExperimentalCoroutinesApi
class UserAccountFragment : Fragment() {

    private lateinit var binding: UserAccountFragmentBinding

    private val viewModel by viewModels<UserAccountViewModel> {
        UserAccountViewModelFactory(ProductServices)
    }

    private lateinit var locationUtils: LocationUtils

    private val handlePicture = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { pictureUri ->
        viewModel.uploadPicture(pictureUri)
    }

    private val handleLocation = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            locationUpdates()
        } else {
            viewModel.showNotification(R.string.require_location)
        }
    }
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.resetImages()
            findNavController().navigateUp()
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            locationResult?.let {

                viewModel.updatedUserData.value?.let { myUser ->

                    val geocoder = Geocoder(context, Locale.getDefault())
                    val lastLocation = locationResult.lastLocation

                    try {
                        val location = geocoder.getFromLocation(
                            lastLocation.latitude,
                            lastLocation.longitude,
                            1
                        )
                        if (location.size > 0) {
                            val address = location[0]
                            viewModel.updateUser(
                                myUser.copy(
                                    location =
                                    MyLocation(
                                        lastLocation.accuracy,
                                        lastLocation.latitude,
                                        lastLocation.longitude,
                                        lastLocation.time,
                                        address.locality ?: address.subAdminArea ?: "",
                                        address.adminArea,
                                        address.countryName
                                    )
                                )
                            )
                        } else {
                            Log.e(tag, "No valid address returned")
                        }

                    } catch (e: IOException) {
                        Log.e(tag, e.message ?: "Error Occurred")
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserAccountFragmentBinding.inflate(inflater, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewmodel = viewModel
        }
        locationUtils = LocationUtils(requireActivity(), locationCallback)

        observeLiveData()
        observeTextChanges()
        setButtonClicks()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activity?.onBackPressedDispatcher?.addCallback(backPressedCallback)
    }

    override fun onPause() {
        backPressedCallback.remove()
        locationUtils.stopLocationUpdates()
        super.onPause()
    }

    private fun observeLiveData() {
        viewModel.apply {
            isLoadingImage.observe(viewLifecycleOwner) { isLoading ->
                binding.shapeableImageView.isEnabled = !isLoading
                binding.imageEditButton.isEnabled = !isLoading
            }

            eventMessage.observe(viewLifecycleOwner, EventObserver {
                Snackbar.make(requireView(), getString(it), Snackbar.LENGTH_LONG).show()
            })
        }
    }

    private fun setButtonClicks() {
        binding.imageEditButton.setOnClickListener {
            handlePicture.launch("image/*")
        }

        binding.btnUpdateLocation.setOnClickListener {

            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED -> {
                    locationUpdates()
                }

                shouldShowRequestPermissionRationale(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) -> {

                    viewModel.showNotification(R.string.require_location)
                }
                else -> {
                    handleLocation.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }

    private fun locationUpdates() {
        if (NetworkAvailabilityUtils.isInternetAvailable(requireContext())) {
            viewModel.toggleLocation()
            locationUtils.startLocationUpdates()
        } else {
            Snackbar.make(requireView(), getString(R.string.no_network_connection),
                Snackbar.LENGTH_LONG).show()
        }

    }

    private fun observeTextChanges() {
        binding.txtFirstName.doOnTextChanged { text, _, _, _ ->
            val currentVal = viewModel.updatedUserData.value
            currentVal?.let {
                viewModel.updateUser(it.copy(firstName = text.toString()))
            }
        }

        binding.txtLastName.doOnTextChanged { text, _, _, _ ->
            val currentVal = viewModel.updatedUserData.value
            currentVal?.let {
                viewModel.updateUser(it.copy(lastName = text.toString()))
            }
        }

        binding.txtDisplayName.doOnTextChanged { text, _, _, _ ->
            val currentVal = viewModel.updatedUserData.value
            currentVal?.let {
                viewModel.updateUser(it.copy(profileDisplayName = text.toString()))
            }
        }

        binding.txtPhone.doOnTextChanged { text, _, _, _ ->
            val currentVal = viewModel.updatedUserData.value
            currentVal?.let {
                viewModel.updateUser(it.copy(phone = text.toString()))
            }
        }
    }
}