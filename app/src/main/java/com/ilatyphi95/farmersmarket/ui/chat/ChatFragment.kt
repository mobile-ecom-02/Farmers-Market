package com.ilatyphi95.farmersmarket.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.databinding.FragmentChatBinding
import com.ilatyphi95.farmersmarket.data.repository.SampleRepository


class ChatFragment : Fragment() {
    lateinit var binding: FragmentChatBinding
    val viewmodel by viewModels<ChatFragmentViewModel> {
        ChatFragmentViewModelFactory("ade", SampleRepository())
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewmodel
            messageEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    viewmodel.newMessage.value = p0.toString()
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
        }

        viewmodel.chatRecycler.observe(viewLifecycleOwner) {
            binding.chatRecyclerView.smoothScrollToPosition(it.size)
        }
        return binding.root
    }

}