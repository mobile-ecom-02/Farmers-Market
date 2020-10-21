package com.ilatyphi95.farmersmarket.data.universaladapter

import androidx.annotation.LayoutRes

data class RecyclerItem(
    val data: Any,
    @LayoutRes val layoutId: Int,
    val variableId: Int
)