package com.example.radin.Utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.radin.ApiService.ApiService
import com.example.radin.ApiService.TokenManager
import com.example.radin.api.RetrofitInstance
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CartService {

    fun addToCart(context: Context, idProduct: Int) {
        val token = TokenManager.getToken(context)
        if (token == null) {
            Toast.makeText(context, "Token is missing", Toast.LENGTH_SHORT).show()
            return
        }

        val addToCartRequest = ApiService.addToCartRequest(idProduct)
        RetrofitInstance.api.addToCart("Bearer $token", addToCartRequest)
            .enqueue(object : Callback<ApiService.addToCartResponse> {
                override fun onResponse(
                    call: Call<ApiService.addToCartResponse>,
                    response: Response<ApiService.addToCartResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val addToCartResponse = response.body()
                        Toast.makeText(
                            context,
                            addToCartResponse?.message ?: "Item telah ditambahkan ke keranjang",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (response.code() == 400) {
                            // Handle 400 Bad Request error
                            val errorBody = response.errorBody()?.string()
                            val errorMessage = JSONObject(errorBody).getString("message")
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("ErrorCuy", "addToCart: ${response.code()}")
                            Toast.makeText(
                                context,
                                response.body()?.message ?: "Gagal menambahkan produk",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiService.addToCartResponse>, t: Throwable) {
                    Toast.makeText(context, "Layanan Tidak Tersedia", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
