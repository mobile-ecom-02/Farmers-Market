package com.ilatyphi95.farmersmarket.data.universaladapter

interface RecyclerItemComparator {
    fun isSameItem(other: Any): Boolean
    fun isSameContent(other: Any): Boolean
}
