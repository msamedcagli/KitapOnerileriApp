package com.example.kitaponerileriapp.network

import com.example.kitaponerileriapp.model.Book
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApiService {

    @GET("kitaplar")
    suspend fun getBooks(): List<Book>

    @GET("kitaplar")
    suspend fun getBooksByCategory(
        @Query("category") category: String
    ): List<Book>

    @GET("kitaplar")
    suspend fun getBooksByAuthor(
        @Query("author") author: String
    ): List<Book>

    @GET("kitaplar")
    suspend fun getBooksByCategoryAndAuthor(
        @Query("category") category: String,
        @Query("author") author: String
    ): List<Book>

    // Yeni eklemeler:
    @GET("kitaplar")
    suspend fun searchBooks(
        @Query("q") query: String
    ): List<Book>

    @GET("kitaplar/{id}")
    suspend fun getBookById(
        @Path("id") id: Int
    ): Book
}
