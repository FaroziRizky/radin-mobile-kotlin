package com.example.radin.MainPageActivity.HistoryPageActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radin.Adapters.HistoryDetailAdapter
import com.example.radin.ApiService.ApiService
import com.example.radin.ApiService.TokenManager
import com.example.radin.PaymentPage
import com.example.radin.R
import com.example.radin.Utils.DateTimeFormatter
import com.example.radin.Utils.PriceFormatter
import com.example.radin.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailHistoryPage : AppCompatActivity() {
    private var totalPay = 0
    private lateinit var total: TextView
    private lateinit var orderedAtTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var userNotesTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var adminNotesTextView: TextView
    private lateinit var finishedAtTextView: TextView
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var addressContainer: LinearLayout
    private lateinit var adminNotesContainer: LinearLayout
    private lateinit var finishContainer: LinearLayout
    private lateinit var buttonContainer: LinearLayout
    private lateinit var btn_back: ImageButton
    private lateinit var btn_payment_info: Button
    private lateinit var confirmPaymentButton: Button
    private lateinit var cancelOrderButton: Button

    private lateinit var historyAdapter: HistoryDetailAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_history_page)

        total = findViewById(R.id.total)
        orderedAtTextView = findViewById(R.id.ordered_at)
        statusTextView = findViewById(R.id.status)
        userNotesTextView = findViewById(R.id.user_notes)
        addressTextView = findViewById(R.id.address)
        adminNotesTextView = findViewById(R.id.admin_notes)
        finishedAtTextView = findViewById(R.id.finished_at)
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu)
        addressContainer = findViewById(R.id.addressContainer)
        adminNotesContainer = findViewById(R.id.adminNotesContainer)
        finishContainer = findViewById(R.id.finishContainer)
        btn_back = findViewById(R.id.btn_back)
        btn_payment_info = findViewById(R.id.btn_payment_info)
        confirmPaymentButton = findViewById(R.id.confirmPaymentButton)
        cancelOrderButton = findViewById(R.id.cancelOrderButton)
        buttonContainer = findViewById(R.id.buttonContainer)

        btn_back.setOnClickListener {
            onBackPressed()
            finish()
        }

        // Parse data from intent or API response
        val id_history = intent.getStringExtra("id_history")

        btn_payment_info.setOnClickListener {
            if (id_history != null) {
                fetchPayment(id_history.toInt())
            }
        }


        confirmPaymentButton.setOnClickListener {
            val token = TokenManager.getToken(this)
            if (token != null) {
                if (id_history != null) {
                    RetrofitInstance.api.confPay(token, id_history.toInt())
                        .enqueue(object : Callback<ApiService.Response> {
                            override fun onResponse(
                                call: Call<ApiService.Response>,
                                response: Response<ApiService.Response>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@DetailHistoryPage,
                                        "Pembayaran dikonfirmasi",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@DetailHistoryPage,
                                        "Tidak bisa mengkonfirmasi pembayaran",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<ApiService.Response>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@DetailHistoryPage,
                                    "Layanan tidak tersedia",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                }
            }
        }

        cancelOrderButton.setOnClickListener {
            val token = TokenManager.getToken(this)
            if (token != null) {
                if (id_history != null) {
                    RetrofitInstance.api.cancelOrder(token, id_history.toInt())
                        .enqueue(object : Callback<ApiService.Response> {
                            override fun onResponse(
                                call: Call<ApiService.Response>,
                                response: Response<ApiService.Response>
                            ) {
                                if (response.isSuccessful) {
                                    Toast.makeText(
                                        this@DetailHistoryPage,
                                        "Pesanan dibatalkan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onBackPressed()
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@DetailHistoryPage,
                                        "Tidak bisa membatalkan pesanan",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(
                                call: Call<ApiService.Response>,
                                t: Throwable
                            ) {
                                Toast.makeText(
                                    this@DetailHistoryPage,
                                    "Layanan tidak tersedia",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        })
                }
            }
        }

        if (id_history != null) {
            fetchHistory(id_history.toInt())
        }









        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchHistory(id_history: Int) {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.detailHistory(token, id_history)
                .enqueue(object : Callback<ApiService.detailResponse> {
                    override fun onResponse(
                        call: Call<ApiService.detailResponse>,
                        response: Response<ApiService.detailResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { detailResponse ->
                                val history = detailResponse.values.history
                                val product = detailResponse.values.product

                                orderedAtTextView.text =
                                    DateTimeFormatter.formatDateTime(history.ordered_at)
                                total.text =
                                    "Rp. " + PriceFormatter.format(history.total.toString())
                                totalPay = history.total


                                when (history.status) {
                                    0 -> {
                                        statusTextView.text = "Belum Bayar"
                                        buttonContainer.visibility = View.VISIBLE
                                        btn_payment_info.visibility = View.VISIBLE
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.pending
                                            )
                                        )
                                    }

                                    1 -> {
                                        statusTextView.text = "Dibatalkan User"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.cbu
                                            )
                                        )
                                    }

                                    2 -> {
                                        statusTextView.text = "Dibatalkan Admin"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.cba
                                            )
                                        )
                                    }

                                    3 -> {
                                        statusTextView.text = "Telah Dibayar"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.paid
                                            )
                                        )
                                    }

                                    4 -> {
                                        statusTextView.text = "Dalam Proses"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.dalam_proses
                                            )
                                        )
                                    }

                                    5 -> {
                                        statusTextView.text = "Siap/diantar"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.ready
                                            )
                                        )
                                    }

                                    6 -> {
                                        statusTextView.text = "Selesai"
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.done
                                            )
                                        )
                                    }

                                    else -> {
                                        statusTextView.text = ""
                                        statusTextView.setTextColor(
                                            ContextCompat.getColor(
                                                this@DetailHistoryPage,
                                                R.color.black
                                            )
                                        )
                                    }
                                }

                                when (history.user_notes) {
                                    "" -> userNotesTextView.text = "-"
                                    null -> userNotesTextView.text = "-"
                                    "null" -> userNotesTextView.text = "-"
                                    else -> userNotesTextView.text = history.user_notes
                                }
                                when (history.address) {
                                    "" -> addressContainer.visibility = View.GONE
                                    null -> addressContainer.visibility = View.GONE
                                    "null" -> addressContainer.visibility = View.GONE
                                    else -> {
                                        addressContainer.visibility = View.VISIBLE
                                        addressTextView.text = history.address
                                    }
                                }
                                when (history.admin_notes) {
                                    "" -> adminNotesContainer.visibility = View.GONE
                                    null -> adminNotesContainer.visibility = View.GONE
                                    "null" -> adminNotesContainer.visibility = View.GONE
                                    else -> {
                                        adminNotesContainer.visibility = View.VISIBLE
                                        adminNotesTextView.text = history.admin_notes
                                    }
                                }
                                when (history.finished_at) {
                                    "" -> finishContainer.visibility = View.GONE
                                    null -> finishContainer.visibility = View.GONE
                                    "null" -> finishContainer.visibility = View.GONE
                                    else -> {
                                        finishContainer.visibility = View.VISIBLE
                                        finishedAtTextView.text = history.finished_at
                                    }
                                }


                                recyclerViewMenu.layoutManager =
                                    LinearLayoutManager(this@DetailHistoryPage)
                                historyAdapter = HistoryDetailAdapter(product)
                                recyclerViewMenu.adapter = historyAdapter
                            }
                        } else {
                            Toast.makeText(
                                this@DetailHistoryPage,
                                "Failed to load cart items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiService.detailResponse>, t: Throwable) {
                        Toast.makeText(
                            this@DetailHistoryPage,
                            "An error occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }

    private fun fetchPayment(id_history: Int) {
        val token = TokenManager.getToken(this)
        if (token != null) {
            RetrofitInstance.api.infoPayment(token)
                .enqueue(object : Callback<ApiService.infoPaymentResponse> {
                    override fun onResponse(
                        call: Call<ApiService.infoPaymentResponse>,
                        response: Response<ApiService.infoPaymentResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let { infoPaymentDetail ->
                                val intent =
                                    Intent(this@DetailHistoryPage, PaymentPage::class.java).apply {
                                        putExtra("total", totalPay.toString())
                                        putExtra("bank_name", infoPaymentDetail.values.bank_name)
                                        putExtra(
                                            "bank_account",
                                            infoPaymentDetail.values.bank_account
                                        )
                                        putExtra("id_history", id_history.toString())
                                        putExtra("from", "1")
                                        Log.d("Error Cuy", "onResponse: $id_history")
                                    }
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(
                                this@DetailHistoryPage,
                                "Failed to load cart items",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.infoPaymentResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(
                            this@DetailHistoryPage,
                            "An error occurred",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }
}