package com.example.radin.Models

data class HistoryModel (
    val id_history: Int,
    val id_user: Int,
    val total: Int,
    val address: String?,
    val user_notes: String?,
    val status: Int,
    val admin_notes: String?,
    val ordered_at: String,
    val finished_at: String?
)