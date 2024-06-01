package com.example.radin.Models

import com.google.gson.annotations.SerializedName

data class ProductModel(
    @SerializedName("id_product") val idProduct: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("type") val type: Int,
    @SerializedName("information") val information: String,
    @SerializedName("picture") val picture: String,
    @SerializedName("price") val price: Int,
    @SerializedName("stock") val stock: Int
)
