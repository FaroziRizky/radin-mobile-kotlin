package com.example.radin.MainPageActivity.ProductPageActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radin.Adapters.ProductAdapter
import com.example.radin.ApiService.ApiService
import com.example.radin.ApiService.TokenManager
import com.example.radin.R
import com.example.radin.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MeatsFragment : Fragment() {
    private lateinit var ProdutMeatsAdapter: ProductAdapter
    private lateinit var productMeatsRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_meats, container, false)
        productMeatsRecyclerView = view.findViewById(R.id.meatsRecyclerView)
        productMeatsRecyclerView.layoutManager = GridLayoutManager(context, 2)

        fetchFruitsProduct()
        return view
    }

    private fun fetchFruitsProduct() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.productMeats(token)
                .enqueue(object : Callback<ApiService.ProductResponse> {
                    override fun onResponse(
                        call: Call<ApiService.ProductResponse>,
                        response: Response<ApiService.ProductResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                ProdutMeatsAdapter = ProductAdapter(it.values)
                                productMeatsRecyclerView.adapter = ProdutMeatsAdapter
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Gagal memuat data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.ProductResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "Layanan tidak tersedia", Toast.LENGTH_SHORT).show()
                    }
                })
        }


    }
}