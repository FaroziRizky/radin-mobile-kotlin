package com.example.radin.MainPageActivity.ProductPageActivity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radin.Adapters.CartAdapter
import com.example.radin.ApiService.TokenManager
import com.example.radin.Models.CartItem
import com.example.radin.Models.CartResponse
import com.example.radin.R
import com.example.radin.api.RetrofitInstance

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartPage : AppCompatActivity(), CartAdapter.OnAmountChangeListener {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalAmount: TextView
    private lateinit var emptyCartText: TextView
    private lateinit var btn_back: ImageButton
    private lateinit var checkoutButton: TextView
    private lateinit var cartItems: MutableList<CartItem>

    private lateinit var totalPriceLayout: RelativeLayout
    private var shippingCost = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_page)

        emptyCartText = findViewById(R.id.emptyCartText)
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmount = findViewById(R.id.totalAmount)

        checkoutButton = findViewById(R.id.checkoutButton)
        btn_back = findViewById(R.id.btn_back)

        totalPriceLayout = findViewById(R.id.totalPriceLayout)

        checkoutButton.setOnClickListener {
            val intent = Intent(this, CheckoutPage::class.java)
            startActivity(intent)
        }


        btn_back.setOnClickListener {
            onBackPressed()
        }



        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchCartItems()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun fetchCartItems() {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.getCartItems(token).enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>, response: Response<CartResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            cartItems = it.values[0].cart_item.toMutableList()
                            if (cartItems.isEmpty()) {
                                // Tampilkan TextView jika keranjang kosong
                                emptyCartText.visibility = View.VISIBLE
                                cartRecyclerView.visibility = View.GONE
                            } else {
                                // Sembunyikan TextView jika keranjang tidak kosong
                                emptyCartText.visibility = View.GONE
                                cartRecyclerView.visibility = View.VISIBLE

                                cartRecyclerView.adapter = CartAdapter(cartItems, this@CartPage)
                                updateTotalAmount(cartItems)
                            }
                        }
                    } else {
                        Toast.makeText(
                            this@CartPage, "Failed to load cart items", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    Toast.makeText(this@CartPage, "An error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    override fun onAmountChanged() {
        updateTotalAmount(cartItems)
    }

    private fun updateTotalAmount(cartItems: List<CartItem>) {
        var total = 0
        for (item in cartItems) {
            total += item.amount * item.price + shippingCost
        }
        totalAmount.text = "Rp $total"

        if (cartItems.isEmpty()) {
            // Tampilkan TextView jika keranjang kosong
            emptyCartText.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
            totalPriceLayout.visibility = View.GONE
            checkoutButton.visibility = View.GONE
        } else {
            // Sembunyikan TextView jika keranjang tidak kosong
            emptyCartText.visibility = View.GONE
            cartRecyclerView.visibility = View.VISIBLE
            totalPriceLayout.visibility = View.VISIBLE
            checkoutButton.visibility = View.VISIBLE
        }
    }
}
