package com.example.kitaponerileriapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitaponerileriapp.model.Book
import com.example.kitaponerileriapp.repository.FavoritesRepository
import kotlinx.coroutines.launch
import com.example.kitaponerileriapp.network.BookApiService
import com.example.kitaponerileriapp.network.BookRepository
import com.example.kitaponerileriapp.network.RetrofitClient
import android.util.Log

class FavoritesViewModel : ViewModel() {

    private val bookApiService: BookApiService = RetrofitClient.apiService
    private val bookRepository: BookRepository = BookRepository(bookApiService)
    private val repository = FavoritesRepository(bookRepository)

    private val _favoriteBooks = MutableLiveData<List<Book>>()
    val favoriteBooks: LiveData<List<Book>> = _favoriteBooks

    private val _favoriteStatusMap = MutableLiveData<Map<String, Boolean>>() // bookId -> favori mi
    val favoriteStatusMap: LiveData<Map<String, Boolean>> = _favoriteStatusMap

    init {
        _favoriteStatusMap.value = emptyMap()
    }

    fun loadFavoriteBooks() {
        viewModelScope.launch {
            Log.d("FavoritesVM", "Loading favorite books...")
            val books = repository.getFavoriteBooks()
            _favoriteBooks.postValue(books)

            val map = books.associate { it.id.toString() to true }
            _favoriteStatusMap.postValue(map)
            Log.d("FavoritesVM", "Favorite books loaded: ${books.size}")
        }
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            Log.d("FavoritesVM", "Toggling favorite for book ID: $bookId")
            val currentStatus = repository.isFavorite(bookId)
            Log.d("FavoritesVM", "Current favorite status for $bookId: $currentStatus")
            repository.toggleFavorite(bookId, currentStatus)

            val updatedMap = _favoriteStatusMap.value?.toMutableMap() ?: mutableMapOf()
            updatedMap[bookId] = !currentStatus
            _favoriteStatusMap.postValue(updatedMap)
            Log.d("FavoritesVM", "Updated favorite status map: $updatedMap")

            // Favori listesini anında güncelle
            val currentFavoriteBooks = _favoriteBooks.value?.toMutableList() ?: mutableListOf()
            if (currentStatus) { // Favoriden çıkarıldı
                currentFavoriteBooks.removeAll { it.id.toString() == bookId }
                Log.d("FavoritesVM", "Removed book $bookId from favorites list.")
            } else { // Favoriye eklendi
                try {
                    val addedBook = bookRepository.getBookById(bookId.toInt()) // API'den kitabı çek
                    currentFavoriteBooks.add(addedBook)
                    Log.d("FavoritesVM", "Added book ${addedBook.id} to favorites list.")
                } catch (e: Exception) {
                    Log.e("FavoritesVM", "Error adding book $bookId to favorites list: ${e.message}")
                }
            }
            _favoriteBooks.postValue(currentFavoriteBooks)
            Log.d("FavoritesVM", "Favorite books list updated. New size: ${currentFavoriteBooks.size}")
        }
    }

    fun checkFavoriteStatus(bookId: String) {
        viewModelScope.launch {
            Log.d("FavoritesVM", "Checking favorite status for book ID: $bookId")
            val isFav = repository.isFavorite(bookId)
            val updatedMap = _favoriteStatusMap.value?.toMutableMap() ?: mutableMapOf()
            updatedMap[bookId] = isFav
            _favoriteStatusMap.postValue(updatedMap)
            Log.d("FavoritesVM", "Favorite status for $bookId: $isFav. Updated map: $updatedMap")
        }
    }
}
