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

    private val _popularRoman = MutableStateFlow<List<Book>>(emptyList())
    val popularRoman: StateFlow<List<Book>> get() = _popularRoman

    private val _popularRomanById = MutableStateFlow<List<Book>>(emptyList())
    val popularRomanById: StateFlow<List<Book>> get() = _popularRomanById

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun fetchAllBooks() {
        viewModelScope.launch {
            try {
                val allBooks = repository.getAllBooks()

                // Mevcut gruplar
                _popularBooks.value = allBooks.take(20)
                _newBooks.value = allBooks.takeLast(35)
                _popularRoman.value = allBooks.filter { it.category?.equals("roman", ignoreCase = true) == true }

                // 🎯 Belirli ID'lere göre en popüler romanlar
                val popularRomanIds = listOf(63, 12, 45, 78, 101, 34, 7, 88, 99, 5)
                _popularRomanById.value = allBooks.filter { it.id in popularRomanIds }

                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Kitaplar yüklenirken hata oluştu: ${e.localizedMessage}"
            }
        }
    }
}
