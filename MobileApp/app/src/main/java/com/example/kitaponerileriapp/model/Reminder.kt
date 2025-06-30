package com.example.kitaponerileriapp.model

data class Reminder(
    val id: Long,
    val date: String,
    val time: String,
    val description: String,
    val pendingIntentId: Int
)