package com.ilatyphi95.farmersmarket.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.repository.SampleRepository
import com.ilatyphi95.farmersmarket.databinding.FragmentMessageBinding
import com.ilatyphi95.farmersmarket.utils.EventObserver

class MessageFragment : Fragment() {

    private val viewmodel by viewModels<MessageViewModel> {
        MessageViewModelFactory(SampleRepository())
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

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
        return binding.root
    }
}