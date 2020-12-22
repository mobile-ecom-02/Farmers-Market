package com.ilatyphi95.farmersmarket.ui.message

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.repository.SampleRepository
import com.ilatyphi95.farmersmarket.databinding.FragmentMessageBinding
import com.ilatyphi95.farmersmarket.firebase.addSnapshotListener
import com.ilatyphi95.farmersmarket.utils.EventObserver

class MessageFragment : Fragment() {

    private val firestore = FirebaseFirestore.getInstance()

    private val viewmodel by viewModels<MessageViewModel> {
        MessageViewModelFactory(SampleRepository())
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil
            .inflate<FragmentMessageBinding>(inflater, R.layout.fragment_message, container, false)

        binding.apply {
            viewModel = viewmodel
            lifecycleOwner = viewLifecycleOwner
        }

        viewmodel.apply {
            eventMessage.observe(viewLifecycleOwner, EventObserver{
                findNavController()
                    .navigate(MessageFragmentDirections.actionNavigationMessageToChatFragment(it))
            })
        }

        val user = FirebaseAuth.getInstance().currentUser

        firestore.collection("users/${user?.uid}/chatList")
            .addSnapshotListener(viewLifecycleOwner) {query, exception ->
                if(exception != null){
                    Log.d(tag, "GetChatMessage: ${exception.message}")
                }

                query?.let {
                    viewmodel.updateMessages(it.toObjects())
                }
            }
        return binding.root
    }
}