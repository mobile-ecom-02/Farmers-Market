package com.ilatyphi95.farmersmarket.ui.ads

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdsPagerAdapter(fragment: Fragment, private val fragmentArray: ArrayList<Fragment>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = fragmentArray.size

    override fun createFragment(position: Int): Fragment = fragmentArray[position]

}