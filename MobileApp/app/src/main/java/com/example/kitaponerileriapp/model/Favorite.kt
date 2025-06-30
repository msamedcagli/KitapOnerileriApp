package com.example.kitaponerileriapp.model

import com.google.firebase.firestore.PropertyName

data class Favorite(
    @get:PropertyName("book_id") @set:PropertyName("book_id") var bookId: String = "",
    @get:PropertyName("user_id") @set:PropertyName("user_id") var userId: String = ""
)
