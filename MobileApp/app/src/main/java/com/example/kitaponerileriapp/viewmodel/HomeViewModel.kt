package com.example.kitaponerileriapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kitaponerileriapp.model.Book
import com.example.kitaponerileriapp.network.BookRepository
import com.example.kitaponerileriapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = BookRepository(RetrofitClient.apiService)

    private val _popularBooks = MutableStateFlow<List<Book>>(emptyList())
    val popularBooks: StateFlow<List<Book>> get() = _popularBooks

    private val _newBooks = MutableStateFlow<List<Book>>(emptyList())
    val newBooks: StateFlow<List<Book>> get() = _newBooks

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun fetchAllBooks() {
        viewModelScope.launch {
            try {
                val allBooks = repository.getAllBooks()

                // Örnek filtreleme:
                _popularBooks.value = allBooks.take(20)      // İlk 10 kitap "popüler"
                _newBooks.value = allBooks.takeLast(20)      // Son 20 kitap "yeni"

                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Kitaplar yüklenirken hata oluştu: ${e.localizedMessage}"
            }
        }
    }
}
