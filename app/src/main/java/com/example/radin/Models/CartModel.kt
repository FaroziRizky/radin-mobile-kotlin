package com.example.radin.Models

data class CartResponse(
    val status: Int,
    val values: List<Cart>
)

data class Cart(
    val id_cart: Int,
    val id_user: Int,
    val checkoutable: Boolean,
    val cart_item: List<CartItem>
)

data class CartItem(
    val id_cart_item: Int,
    val id_product: Int,
    var amount: Int,
    val product_name: String,
    val type: Int,
    val information: String,
    val picture: String,
    val price: Int,
    val stock: Int,
    val checkoutable: Boolean
)
