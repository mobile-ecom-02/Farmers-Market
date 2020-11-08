package com.ilatyphi95.farmersmarket.ui.registration


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.databinding.FragmentSplashBinding
import com.ilatyphi95.farmersmarket.utils.*
import kotlinx.coroutines.*

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val userStateObserver = Observer<FirebaseAuthUserState> { userState ->
        when (userState) {
            is UserSignedIn -> {
                if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {

                    findNavController().navigate(
                        SplashFragmentDirections.actionSplashFragment2ToHomeActivity())

                } else {
                    sendVerificationEmail(requireView())
                }
            }
            UserSignedOut -> {
                val extras = FragmentNavigatorExtras(
                    binding.treeImage to "treeImage",
                    binding.root to "headerContainer"
                )
                findNavController().navigate(
                    R.id.action_splashFragment2_to_loginFragment,
                    null,
                    null,
                    extras
                )
            }
            UserUnknown -> {
                // remain on splash screen
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            delay(1500)

            FirebaseAuth.getInstance().newFirebaseAuthStateLiveData()
                .observe(viewLifecycleOwner, userStateObserver)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}