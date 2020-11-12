package com.ilatyphi95.farmersmarket.ui.settings

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.entities.MyLocation
import com.ilatyphi95.farmersmarket.utils.LocationUtils
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class SettingsFragment : Fragment() {

    private val TAG: String? = this.tag

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        settingsViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        root.findViewById<Button>(R.id.btnLocation).setOnClickListener {
            LocationUtils.getLastLocation(requireActivity()){location ->
                val geocoder = Geocoder(context, Locale.getDefault())

                try {
                    val myLocation = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    if (myLocation.size > 0) {
                        val address = myLocation[0]
                        val map = HashMap<String, Any>()
                        map["location"] = MyLocation(
                            location.accuracy,
                            location.latitude,
                            location.longitude,
                            location.time,
                            address.subAdminArea,
                            address.adminArea,
                            address.countryName)

                        Toast.makeText(context, address.adminArea, Toast.LENGTH_LONG).show()

                        FirebaseFirestore.getInstance()
                            .document("users/${FirebaseAuth.getInstance().currentUser?.uid}")
                            .set(map, SetOptions.merge())

                    } else {
                        Log.e( TAG, "No valid address returned")
                    }

                } catch (e: IOException) {
                    Log.e( TAG, e.message ?: "Error Occurred")
                }
            }
        }
        root.findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController()
                .navigate(SettingsFragmentDirections.actionNavigationSettingsToMainActivity())
        }
        return root
    }
}