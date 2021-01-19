package com.ilatyphi95.farmersmarket.utils

import android.net.Uri

enum class ImageStatus {
    UPLOADED,
    DELETED,
    ADDED
}

sealed class ProductImage(val status: ImageStatus)

data class ImageUploaded(val imageAddress: String) : ProductImage(ImageStatus.UPLOADED)
data class ImageDeleted(val imageAddress: String) : ProductImage(ImageStatus.DELETED)
data class ImageAdded(val imageAddress: Uri) : ProductImage(ImageStatus.ADDED)