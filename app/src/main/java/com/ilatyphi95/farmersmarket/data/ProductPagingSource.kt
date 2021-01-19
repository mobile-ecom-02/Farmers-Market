package com.ilatyphi95.farmersmarket.data

import androidx.paging.PagingSource
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.ilatyphi95.farmersmarket.data.entities.Product
import kotlinx.coroutines.tasks.await

class ProductPagingSource(private val query: Query) : PagingSource<QuerySnapshot, Product>() {

    companion object {
        const val PAGE_SIZE = 15
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, Product> {

        return try {

            val currentPage = params.key ?: query
                .limit(PAGE_SIZE.toLong())
                .get()
                .await()

            val lastDocumentSnapshot = currentPage.documents[currentPage.size() - 1]

            val nextPage = query.limit(PAGE_SIZE.toLong()).startAfter(lastDocumentSnapshot)
                .get()
                .await()

            LoadResult.Page(
                data = currentPage.toObjects(),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}