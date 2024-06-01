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

class GroceriesFragment : Fragment() {
    private lateinit var ProdutGroceriesAdapter: ProductAdapter
    private lateinit var productGroceriesRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_groceries, container, false)
        productGroceriesRecyclerView = view.findViewById(R.id.groceriesRecyclerView)
        productGroceriesRecyclerView.layoutManager = GridLayoutManager(context, 2)

        fetchFruitsProduct()
        return view
    }

    private fun fetchFruitsProduct() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.productGroceries(token)
                .enqueue(object : Callback<ApiService.ProductResponse> {
                    override fun onResponse(
                        call: Call<ApiService.ProductResponse>,
                        response: Response<ApiService.ProductResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                ProdutGroceriesAdapter = ProductAdapter(it.values)
                                productGroceriesRecyclerView.adapter = ProdutGroceriesAdapter
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Layanan tidak tersedia",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.ProductResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }


    }

}