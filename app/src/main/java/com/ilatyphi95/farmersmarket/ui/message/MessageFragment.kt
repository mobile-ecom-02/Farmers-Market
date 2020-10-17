package com.ilatyphi95.farmersmarket.ui.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ilatyphi95.farmersmarket.ProductGenerator
import com.ilatyphi95.farmersmarket.R

class MessageFragment : Fragment() {

    private lateinit var messageViewModel: MessageViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        messageViewModel =
                ViewModelProviders.of(this).get(MessageViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_message, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        messageViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })

        root.findViewById<Button>(R.id.btnProduct).setOnClickListener {
            findNavController().navigate(MessageFragmentDirections.actionNavigationMessageToProductFragment(
               ProductGenerator.getList()[0])
            )
        }
        return root
    }
}