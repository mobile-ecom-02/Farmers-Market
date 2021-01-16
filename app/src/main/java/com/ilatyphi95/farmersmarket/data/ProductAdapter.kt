package com.ilatyphi95.farmersmarket.data

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.PagingDataAdapter
import com.ilatyphi95.farmersmarket.R
import com.ilatyphi95.farmersmarket.data.universaladapter.BindingViewHolder
import com.ilatyphi95.farmersmarket.data.universaladapter.DiffCallback
import com.ilatyphi95.farmersmarket.data.universaladapter.RecyclerItem

class ProductAdapter() :
    PagingDataAdapter<RecyclerItem, BindingViewHolder>(DiffCallback()) {
    override fun getItemViewType(position: Int): Int {

        return getItem(position)?.layoutId ?: R.layout.search_product_placeholder
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BindingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding = DataBindingUtil.inflate(inflater, viewType, parent, false)
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder,
        position: Int
    ) {
        holder.run {
            getItem(position)?.bind(binding)
            if (binding.hasPendingBindings()) {
                binding.executePendingBindings()
            }
        }
    }
}

private fun RecyclerItem.bind(binding: ViewDataBinding) {
    val isVariableFound = binding.setVariable(variableId, data)
    if (isVariableFound.not()) {
        throw IllegalStateException(
            buildErrorMessage(variableId, binding)
        )
    }
}

private fun buildErrorMessage(
    variableId: Int,
    binding: ViewDataBinding
): String {
    val variableName = DataBindingUtil.convertBrIdToString(variableId)
    val className = binding::class.simpleName
    return "Failed to find variable='$variableName' in the following databinding layout: $className"
}