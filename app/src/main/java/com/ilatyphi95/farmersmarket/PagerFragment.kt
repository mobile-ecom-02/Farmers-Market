package com.ilatyphi95.farmersmarket

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.ilatyphi95.farmersmarket.databinding.FragmentLoginBinding
import com.ilatyphi95.farmersmarket.databinding.FragmentPagerBinding


class PagerFragment : Fragment() {
    private var _binding: FragmentPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentArray = arrayListOf(SalesFragment(), PurchaseFragment())

        val pager = binding.pager.apply {
            adapter = SalesPurchasePagerAdapter(this@PagerFragment, fragmentArray)
        }
        val tabLayout = binding.tabLayout
        TabLayoutMediator(tabLayout, pager,){tab, position ->
            if (position == 0){
                tab.text = "Sales"
            }
            else{
                tab.text = "Purchase"
            }
        }.attach()
    }

}