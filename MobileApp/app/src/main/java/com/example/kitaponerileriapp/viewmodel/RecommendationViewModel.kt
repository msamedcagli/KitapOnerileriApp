package com.example.kitaponerileriapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitaponerileriapp.model.Book
import com.example.kitaponerileriapp.network.BookRepository
import com.example.kitaponerileriapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class RecommendationViewModel : ViewModel() {

    private val repository = BookRepository(RetrofitClient.apiService)

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> get() = _categories

    private val _filteredBooks = MutableStateFlow<List<Book>>(emptyList())
    val filteredBooks: StateFlow<List<Book>> get() = _filteredBooks

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val books = repository.getAllBooks()
                _categories.value = books.map { it.category }.distinct().sorted()
            } catch (e: Exception) {
                _errorMessage.value = "Kategoriler yüklenirken hata oluştu: ${e.localizedMessage}"
            }
        }
    }

    // Sadece kategoriye göre rastgele kitap seçen fonksiyon:
    fun fetchFilteredBooks(category: String) {
        viewModelScope.launch {
            try {
                val allBooks = repository.getAllBooks()
                val filteredBooks = allBooks.filter {
                    it.category.equals(category, ignoreCase = true)
                }
                if (filteredBooks.isNotEmpty()) {
                    // Rastgele bir kitap seç
                    val randomIndex = Random.nextInt(filteredBooks.size)
                    _filteredBooks.value = listOf(filteredBooks[randomIndex])
                } else {
                    _filteredBooks.value = emptyList()
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    // Öneri sonrası listeyi temizle (isteğe bağlı, öneriyi yenilemek için)
    fun clearFilteredBooks() {
        _filteredBooks.value = emptyList()
    }
}
