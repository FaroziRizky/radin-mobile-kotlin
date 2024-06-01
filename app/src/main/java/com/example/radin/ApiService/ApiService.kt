package com.example.radin.ApiService


import com.example.radin.Models.AkunModel
import com.example.radin.Models.CartResponse
import com.example.radin.Models.HistoryModel
import com.example.radin.Models.ItemHistoryModel
import com.example.radin.Models.LoginRequest
import com.example.radin.Models.LoginResponse
import com.example.radin.Models.OrderModel
import com.example.radin.Models.ProductModel
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/api/user/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/user/register")
    fun register(
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @GET("/api/user/profile")
    fun profile(@Header("Authorization") token: String): Call<profileResponse>
    data class profileResponse(val status: Int, val values: List<AkunModel>)

    @PUT("/api/user/profile/edit")
    fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @PUT("/api/user/profile/password")
    fun updatePassword(
        @Header("Authorization") token: String,
        @Body requestBody: Map<String, String>
    ): Call<Void?>?

    @GET("/api/user/products/recommended")
    fun recommendedProducts(@Header("Authorization") token: String): Call<recomendedResponse>
    data class recomendedResponse(val status: Int, val values: List<ProductModel>)

    @GET("/api/user/products/recommended/sembako")
    fun recommendedGroceries(@Header("Authorization") token: String): Call<recomendedResponse>

    @GET("/api/user/products/recommended/daging")
    fun recommendedMeats(@Header("Authorization") token: String): Call<recomendedResponse>

    @GET("/api/user/products/recommended/buah")
    fun recommendedFruits(@Header("Authorization") token: String): Call<recomendedResponse>

    @GET("/api/user/product/{id_menu}")
    fun getProductDetails(
        @Header("Authorization") token: String,
        @Path("id_menu") idMenu: Int
    ): Call<productDetail>

    data class productDetail(val status: Int, val values: List<ProductModel>)


    data class ProductResponse(val status: Int, val values: List<ProductModel>)

    @GET("/api/user/products/sembako")
    fun productGroceries(@Header("Authorization") token: String): Call<ProductResponse>

    @GET("/api/user/products/daging")
    fun productMeats(@Header("Authorization") token: String): Call<ProductResponse>

    @GET("/api/user/products/buah")
    fun productFruits(@Header("Authorization") token: String): Call<ProductResponse>

    @POST("/api/user/cart/item/add-to-cart")
    fun addToCart(
        @Header("Authorization") token: String,
        @Body request: addToCartRequest
    ): Call<addToCartResponse>

    data class addToCartRequest(val id_product: Int)
    data class addToCartResponse(val status: Int, val message: String)

    @GET("/api/user/cart")
    fun getCartItems(
        @Header("Authorization") token: String
    ): Call<CartResponse>

    @PUT("/api/user/cart/item/setamount")
    fun setCartItemAmount(
        @Header("Authorization") token: String,
        @Body request: Map<String, Int>
    ): Call<setCartItemAmountResponse>

    data class setCartItemAmountResponse(
        val status: Int,
        val message: String
    )

    @POST("/api/user/checkout")
    fun checkout(
        @Header("Authorization") token: String,
        @Body request: checkoutRequest
    ): Call<checkoutResponse>

    data class checkoutRequest(val address: String, val user_notes: String)
    data class checkoutResponse(
        val status: Int,
        val message: String,
        val bank_name: String,
        val bank_account: String,
        val total: Int,
        val iHistory: Int
    )

    @PUT("/api/user/cancel/{id_history}")
    fun cancelOrder(
        @Header("Authorization") token: String,
        @Path("id_history") id_history: Int
    ): Call<Response>

    @PUT("/api/user/confirm/{id_history}")
    fun confPay(
        @Header("Authorization") token: String,
        @Path("id_history") id_history: Int
    ): Call<Response>

    data class Response(
        val status: Int,
        val message: String
    )

    @GET("/api/user/orders/pending")
    fun unpaidHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>

    @GET("/api/user/orders/process")
    fun processHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>

    @GET("/api/user/orders")
    fun allHistory(
        @Header("Authorization") token: String
    ): Call<HistoryResponse>

    data class HistoryResponse(val status: Int, val values: List<OrderModel>)


    @GET("/api/user/order/{id_history}")
    fun detailHistory(
        @Header("Authorization") token: String,
        @Path("id_history") id_history: Int
    ): Call<detailResponse>

    data class detailResponse(
        val status: Int,
        val values: ValuesDetail
    )

    data class ValuesDetail(
        @SerializedName("history") val history: HistoryModel,
        @SerializedName("product") val product: List<ItemHistoryModel>
    )

    @GET("/api/user/information")
    fun infoPayment(
        @Header("Authorization") token: String,
    ): Call<infoPaymentResponse>

    data class infoPaymentResponse(
        val status: Int,
        val values: infoPaymentDetail
    )

    data class infoPaymentDetail(
        val id_information: Int,
        val bank_name: String,
        val bank_account: String
    )
}