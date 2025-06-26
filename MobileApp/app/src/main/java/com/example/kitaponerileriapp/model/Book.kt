package com.example.kitaponerileriapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val category: String,
    val summary: String,

    @SerializedName("image")
    val image: String
) : Parcelable


