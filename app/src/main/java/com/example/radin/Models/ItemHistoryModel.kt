package com.example.radin.Models

data class ItemHistoryModel(
    val id_item_history: Int,
    val id_history: Int,
    val name_product: String,
    val type: Int,
    val price: Double,
    val amount: Int
)