package com.example.radin.MainPageActivity.ProductPageActivity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.radin.Adapters.CheckoutAdapter
import com.example.radin.ApiService.ApiService
import com.example.radin.ApiService.TokenManager
import com.example.radin.Models.CartItem
import com.example.radin.Models.CartResponse
import com.example.radin.PaymentPage
import com.example.radin.R
import com.example.radin.api.RetrofitInstance
import com.google.android.material.button.MaterialButtonToggleGroup
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckoutPage : AppCompatActivity() {

    private lateinit var checkoutRecyclerView: RecyclerView
    private lateinit var totalAmount: TextView
    private lateinit var shppingAmount: TextView

    private lateinit var btn_back: ImageButton
    private lateinit var checkoutButton: TextView
    private lateinit var notes: TextView
    private lateinit var checkoutItems: MutableList<CartItem>
    private lateinit var alamatEditText: EditText
    private lateinit var toggleGroup: MaterialButtonToggleGroup
    private lateinit var shippingContainer: LinearLayout
    private lateinit var totalPriceLayout: RelativeLayout
    private var shippingCost = 0

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout_page)

        checkoutRecyclerView = findViewById(R.id.checkoutRecyclerView)
        totalAmount = findViewById(R.id.totalAmount)
        shppingAmount = findViewById(R.id.shppingAmount)
        checkoutButton = findViewById(R.id.checkoutButton)
        btn_back = findViewById(R.id.btn_back)
        alamatEditText = findViewById(R.id.alamat)
        notes = findViewById(R.id.notes)
        toggleGroup = findViewById(R.id.toggleGroup)
        shippingContainer = findViewById(R.id.shippingContainer)
        totalPriceLayout = findViewById(R.id.totalPriceLayout)

        checkoutButton.setOnClickListener {
            checkout()
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }

        toggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId == R.id.button_delivery && isChecked) {
                shppingAmount.text = "Rp 5000"
                shippingCost = 5000
                updateTotalAmount()
                alamatEditText.visibility = View.VISIBLE
                shippingContainer.visibility = View.VISIBLE
                val params = totalPriceLayout.layoutParams as LinearLayout.LayoutParams
                params.weight = 1.24f
                totalPriceLayout.layoutParams = params
            } else if (checkedId == R.id.button_pick_up && isChecked) {
                shppingAmount.text = ""
                shippingCost = 0
                updateTotalAmount()
                alamatEditText.text = null
                alamatEditText.visibility = View.GONE
                shippingContainer.visibility = View.GONE
                val params = totalPriceLayout.layoutParams as LinearLayout.LayoutParams
                params.weight = 1f
                totalPriceLayout.layoutParams = params
            }
        }

        checkoutRecyclerView.layoutManager = LinearLayoutManager(this)
        fetchCartItems()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun checkout() {
        val token = TokenManager.getToken(this)
        val address = alamatEditText.text.toString()
        val notes = notes.text.toString()
        val reqBody = ApiService.checkoutRequest(
            address = address,
            user_notes = notes
        )
        if (token != null) {
            RetrofitInstance.api.checkout(token, reqBody)
                .enqueue(object : Callback<ApiService.checkoutResponse> {
                    override fun onResponse(
                        call: Call<ApiService.checkoutResponse>,
                        response: Response<ApiService.checkoutResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                val intent = Intent(this@CheckoutPage, PaymentPage::class.java).apply {
                                    putExtra("total", it.total.toString())
                                    putExtra("bank_name", it.bank_name)
                                    putExtra("bank_account", it.bank_account)
                                    putExtra("id_history", it.iHistory.toString())
                                    putExtra("from", "0")
                                }
                                startActivity(intent)
                                finish()
                                finish()
                            }
                        } else {
                            if (response.code() == 400) {
                                // Handle 400 Bad Request error
                                val errorBody = response.errorBody()?.string()
                                val errorMessage = JSONObject(errorBody).getString("message")
                                Toast.makeText(this@CheckoutPage, errorMessage, Toast.LENGTH_SHORT).show()
                            } else {
                                Log.d("ErrorCuy", "addToCart: ${response.code()}")
                                Toast.makeText(
                                    this@CheckoutPage,
                                    response.body()?.message ?: "Gagal melakukan checkout",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiService.checkoutResponse>, t: Throwable) {
                        Toast.makeText(
                            this@CheckoutPage,
                            "Layanan tidak tersedia",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun fetchCartItems() {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.getCartItems(token)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                checkoutItems = it.values[0].cart_item.toMutableList()
                                if (checkoutItems.isEmpty()) {
                                    checkoutRecyclerView.visibility = View.GONE
                                    toggleGroup.visibility = View.GONE
                                } else {
                                    checkoutRecyclerView.visibility = View.VISIBLE
                                    toggleGroup.visibility = View.VISIBLE
                                    checkoutRecyclerView.adapter = CheckoutAdapter(checkoutItems)
                                    updateTotalAmount()
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@CheckoutPage,
                                "Failed to load cart items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        Toast.makeText(
                            this@CheckoutPage,
                            "An error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun updateTotalAmount() {
        var totalPrice = 0
        for (item in checkoutItems) {
            totalPrice += item.price * item.amount
        }
        totalAmount.text = "Rp ${totalPrice + shippingCost}"
    }
}
