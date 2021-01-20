package com.ilatyphi95.farmersmarket.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.findNavController
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.repository.FirebaseMessagingService

class PreferenceFragment : PreferenceFragmentCompat() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nightPref = findPreference<ListPreference>(getString(R.string.pref_key_night))

        nightPref?.setOnPreferenceChangeListener{ _, newValue ->
            val night = getString(R.string.pref_dark_theme_on)
            val day = getString(R.string.pref_dark_theme_off)

            if(newValue is String) {
                when(newValue.toString()) {
                    day -> updateTheme(AppCompatDelegate.MODE_NIGHT_NO)
                    night -> updateTheme(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            true
        }

        val logout = findPreference<PreferenceScreen>(getString(R.string.key_log_out))
        logout?.setOnPreferenceClickListener {

            // remove device token from server
            FirebaseMessagingService.useToken {
                FirebaseMessagingService.removeRegistrationFromServer(it)
                FirebaseAuth.getInstance().signOut()
            }

            findNavController().navigate(
                R.id.action_preferenceFragment_to_loginFragment2
            )
            true
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    private fun updateTheme(mode: Int) : Boolean {
        AppCompatDelegate.setDefaultNightMode(mode)
        requireActivity().recreate()
        return true
    }
}