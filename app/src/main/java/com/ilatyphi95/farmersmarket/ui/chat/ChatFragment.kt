package com.ilatyphi95.farmersmarket.ui.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.databinding.FragmentChatBinding
import com.ilatyphi95.farmersmarket.firebase.addSnapshotListener


class ChatFragment : Fragment() {
    lateinit var binding: FragmentChatBinding

    private val fireStore = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    private val args by navArgs<ChatFragmentArgs>()

    val viewmodel by viewModels<ChatFragmentViewModel> {
        ChatFragmentViewModelFactory(args.messageId)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewmodel
        }

        fireStore.collection("messages/${args.messageId}/chatMessages")
            .addSnapshotListener(viewLifecycleOwner) { query, exception ->
                if(exception != null) {
                    Log.d(tag, "setUpFirestoreListeners: ${exception.message}")
                }

                query?.let {
                    viewmodel.updateChat(it.toObjects())
                }

                // reset counter
                val map = HashMap<String, Int>()
                map["counter"] = 0

                fireStore.document("users/${user?.uid}/chatList/${args.messageId}")
                    .set(map, SetOptions.merge())
            }

        viewmodel.chatRecycler.observe(viewLifecycleOwner) {
            binding.chatRecyclerView.smoothScrollToPosition(it.size)
        }
        return binding.root
    }

}