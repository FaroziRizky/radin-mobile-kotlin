package com.example.radin.MainPageActivity.HistoryPageActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.radin.Adapters.HistoryAdapter
import com.example.radin.ApiService.ApiService
import com.example.radin.ApiService.TokenManager
import com.example.radin.R
import com.example.radin.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HistoryAllFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var noItem: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_history_all, container, false)
        noItem = view.findViewById(R.id.noItem)
        // Initialize RecyclerView and Adapter
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            fetchHistory()
        }

        fetchHistory()


        return view
    }

    private fun fetchHistory() {
        swipeRefreshLayout.isRefreshing = true
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.allHistory(token)
                .enqueue(object : Callback<ApiService.HistoryResponse> {
                    override fun onResponse(
                        call: Call<ApiService.HistoryResponse>,
                        response: Response<ApiService.HistoryResponse>
                    ) {
                        swipeRefreshLayout.isRefreshing = false
                        if (response.isSuccessful) {
                            response.body()?.let {
                                if (it.values.size == 0) {
                                    noItem.visibility = View.VISIBLE
                                    recyclerView.visibility = View.GONE
                                }else{
                                    recyclerView.visibility = View.VISIBLE
                                    historyAdapter = HistoryAdapter(it.values)
                                    recyclerView.adapter = historyAdapter
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to load unpaid orders",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.HistoryResponse>,
                        t: Throwable
                    ) {
                        swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            swipeRefreshLayout.isRefreshing = false
            Toast.makeText(context, "Token is null", Toast.LENGTH_SHORT).show()
        }
    }


}