package com.example.kitaponerileriapp.network

import com.example.kitaponerileriapp.model.Book

class BookRepository(private val apiService: BookApiService) {

    suspend fun getAllBooks(): List<Book> = apiService.getBooks()

    suspend fun getBooksByCategory(category: String): List<Book> =
        apiService.getBooksByCategory(category)

    suspend fun getBooksByAuthor(author: String): List<Book> =
        apiService.getBooksByAuthor(author)

    suspend fun getBooksByCategoryAndAuthor(category: String, author: String): List<Book> =
        apiService.getBooksByCategoryAndAuthor(category, author)

    suspend fun searchBooks(query: String): List<Book> =
        apiService.searchBooks(query)

    suspend fun getBookById(id: Int): Book =
        apiService.getBookById(id)
}
