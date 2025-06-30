package com.example.kitaponerileriapp.repository

import com.example.kitaponerileriapp.model.Book
import com.example.kitaponerileriapp.model.Favorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.kitaponerileriapp.network.BookRepository
import android.util.Log

class FavoritesRepository(private val bookRepository: BookRepository) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserFavoritesCollection() = auth.currentUser?.uid?.let {
        Log.d("FavoritesRepo", "User ID: $it")
        firestore.collection("users").document(it).collection("favorites")
    }

    suspend fun toggleFavorite(bookId: String, isCurrentlyFavorite: Boolean) {
        Log.d("FavoritesRepo", "toggleFavorite called for bookId: $bookId, currentStatus: $isCurrentlyFavorite")
        val userFavoritesCollection = getUserFavoritesCollection()
        if (userFavoritesCollection == null) {
            Log.e("FavoritesRepo", "User not authenticated, cannot toggle favorite.")
            return
        }

        val favoriteQuery = userFavoritesCollection
            .whereEqualTo("book_id", bookId)
            .get()
            .await()

        if (favoriteQuery.isEmpty && !isCurrentlyFavorite) {
            val favorite = Favorite(bookId = bookId, userId = auth.currentUser?.uid ?: "")
            userFavoritesCollection.add(favorite).await()
            Log.d("FavoritesRepo", "Book $bookId added to favorites.")
        } else if (!favoriteQuery.isEmpty && isCurrentlyFavorite) {
            for (document in favoriteQuery.documents) {
                userFavoritesCollection.document(document.id).delete().await()
                Log.d("FavoritesRepo", "Book $bookId removed from favorites.")
            }
        }
    }

    suspend fun isFavorite(bookId: String): Boolean {
        Log.d("FavoritesRepo", "isFavorite called for bookId: $bookId")
        val userFavoritesCollection = getUserFavoritesCollection()
        if (userFavoritesCollection == null) {
            Log.e("FavoritesRepo", "User not authenticated, cannot check favorite status.")
            return false
        }

        val favoriteQuery = userFavoritesCollection
            .whereEqualTo("book_id", bookId)
            .get()
            .await()
        val result = !favoriteQuery.isEmpty
        Log.d("FavoritesRepo", "Book $bookId is favorite: $result")
        return result
    }

    suspend fun getFavoriteBooks(): List<Book> {
        Log.d("FavoritesRepo", "getFavoriteBooks called.")
        val userFavoritesCollection = getUserFavoritesCollection()
        if (userFavoritesCollection == null) {
            Log.e("FavoritesRepo", "User not authenticated, cannot get favorite books.")
            return emptyList()
        }

        val favoriteIds = userFavoritesCollection
            .get()
            .await()
            .toObjects(Favorite::class.java)
            .map { it.bookId.toIntOrNull() } // String'den Int'e dönüştürme
            .filterNotNull() // null olanları filtrele

        Log.d("FavoritesRepo", "Fetched favorite IDs: $favoriteIds")

        if (favoriteIds.isEmpty()) {
            Log.d("FavoritesRepo", "No favorite IDs found.")
            return emptyList()
        }

        val favoriteBooks = mutableListOf<Book>()
        for (id in favoriteIds) {
            try {
                val book = bookRepository.getBookById(id)
                favoriteBooks.add(book)
                Log.d("FavoritesRepo", "Fetched book from API: ${book.title}")
            } catch (e: Exception) {
                Log.e("FavoritesRepo", "Error fetching book with ID $id from API: ${e.message}")
                e.printStackTrace()
            }
        }
        Log.d("FavoritesRepo", "Returning ${favoriteBooks.size} favorite books.")
        return favoriteBooks
    }
}
