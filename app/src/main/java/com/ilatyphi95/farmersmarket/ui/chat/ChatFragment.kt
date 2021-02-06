package com.ilatyphi95.farmersmarket.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.databinding.FragmentChatBinding
import com.ilatyphi95.farmersmarket.firebase.services.ProductServices
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ChatFragment : Fragment() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var binding: FragmentChatBinding

    private val args by navArgs<ChatFragmentArgs>()

    val viewmodel by viewModels<ChatFragmentViewModel> {
        ChatFragmentViewModelFactory(args.messageId, ProductServices)
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

        viewmodel.chatRecycler.observe(viewLifecycleOwner) {
            binding.chatRecyclerView.smoothScrollToPosition(it.size)
        }

        return binding.root
    }

    override fun onPause() {
        tearDownCustomToolbar()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        setUpCustomToolbar()
    }

    private fun setUpCustomToolbar() {
        toolbar = binding.chatToolBar
        toolbar.title = ""
        val navHostFragment = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            view?.findNavController()?.navigateUp()
        }
    }

    private fun tearDownCustomToolbar() {
        val toolbar = requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val navHostFragment = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

        (requireActivity() as? AppCompatActivity)?.setSupportActionBar(toolbar)
    }

}