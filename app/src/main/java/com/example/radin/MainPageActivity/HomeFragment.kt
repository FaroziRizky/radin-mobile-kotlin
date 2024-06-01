package com.example.radin.MainPageActivity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.radin.Adapters.RecommendedProductsAdapter
import com.example.radin.ApiService.ApiService
import com.example.radin.ApiService.TokenManager
import com.example.radin.LoginPage
import com.example.radin.MainPage
import com.example.radin.R
import com.example.radin.api.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var recommendedProductsRecyclerView: RecyclerView
    private lateinit var recomendedGroceriesRecyclerView: RecyclerView
    private lateinit var recomendedMeatRecyclerView: RecyclerView
    private lateinit var recomendedFruitRecyclerView: RecyclerView
    private lateinit var recommendedProductsAdapter: RecommendedProductsAdapter
    private lateinit var recomendedGroceriesAdapter: RecommendedProductsAdapter
    private lateinit var recomendedMeatAdapter: RecommendedProductsAdapter
    private lateinit var recomendedFruitAdapter: RecommendedProductsAdapter
    private lateinit var nama: TextView
    private lateinit var seeAll: TextView
    private lateinit var seeAllGroceries: TextView
    private lateinit var seeAllMeats: TextView
    private lateinit var seeAllFruits: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var contentLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        contentLayout = view.findViewById(R.id.contentLayout)

        recommendedProductsRecyclerView = view.findViewById(R.id.recommendedProductsRecyclerView)
        recommendedProductsRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recomendedGroceriesRecyclerView = view.findViewById(R.id.recomendedGroceriesRecyclerView)
        recomendedGroceriesRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recomendedMeatRecyclerView = view.findViewById(R.id.recomendedMeatRecyclerView)
        recomendedMeatRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        recomendedFruitRecyclerView = view.findViewById(R.id.recomendedFruitRecyclerView)
        recomendedFruitRecyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        nama = view.findViewById(R.id.user_name)
        seeAll = view.findViewById(R.id.seeAll)
        seeAllGroceries = view.findViewById(R.id.seeAllGroceries)
        seeAllMeats = view.findViewById(R.id.seeAllMeats)
        seeAllFruits = view.findViewById(R.id.seeAllFruits)

        seeAll.setOnClickListener {
            val intent = Intent(context, MainPage::class.java)
            intent.putExtra(MainPage.EXTRA_FRAGMENT_ID, MainPage.FRAGMENT_MENU)
            intent.putExtra(MainPage.EXTRA_TAB_ID, MainPage.TAB_SEMBAKO)
            startActivity(intent)
        }
        seeAllGroceries.setOnClickListener {
            val intent = Intent(context, MainPage::class.java)
            intent.putExtra(MainPage.EXTRA_FRAGMENT_ID, MainPage.FRAGMENT_MENU)
            intent.putExtra(MainPage.EXTRA_TAB_ID, MainPage.TAB_SEMBAKO)
            startActivity(intent)
        }
        seeAllMeats.setOnClickListener {
            val intent = Intent(context, MainPage::class.java)
            intent.putExtra(MainPage.EXTRA_FRAGMENT_ID, MainPage.FRAGMENT_MENU)
            intent.putExtra(MainPage.EXTRA_TAB_ID, MainPage.TAB_DAGING)
            startActivity(intent)
        }
        seeAllFruits.setOnClickListener {
            val intent = Intent(context, MainPage::class.java)
            intent.putExtra(MainPage.EXTRA_FRAGMENT_ID, MainPage.FRAGMENT_MENU)
            intent.putExtra(MainPage.EXTRA_TAB_ID, MainPage.TAB_BUAH)
            startActivity(intent)
        }

        fetchProfile()
        fetchRecommendedProducts()
        fetchRecommendedGroceries()
        fetchRecommendedMeats()
        fetchRecommendedFruits()

        return view
    }

    private fun fetchProfile() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.profile(token)
                .enqueue(object : Callback<ApiService.profileResponse> {
                    override fun onResponse(
                        call: Call<ApiService.profileResponse>,
                        response: Response<ApiService.profileResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                nama.setText(it.values[0].fullname)
                                checkAllDataFetched()
                            }
                        } else {
                            context?.let { TokenManager.clearToken(it) }
                            val intent = Intent(context, LoginPage::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            activity?.finish()
                            Toast.makeText(
                                context,
                                "Sesi telah habis",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiService.profileResponse>, t: Throwable) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchRecommendedProducts() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.recommendedProducts(token)
                .enqueue(object : Callback<ApiService.recomendedResponse> {
                    override fun onResponse(
                        call: Call<ApiService.recomendedResponse>,
                        response: Response<ApiService.recomendedResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                recommendedProductsAdapter = RecommendedProductsAdapter(it.values)
                                recommendedProductsRecyclerView.adapter = recommendedProductsAdapter
                                checkAllDataFetched()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Sesi Telah Habis",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.recomendedResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchRecommendedGroceries() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.recommendedGroceries(token)
                .enqueue(object : Callback<ApiService.recomendedResponse> {
                    override fun onResponse(
                        call: Call<ApiService.recomendedResponse>,
                        response: Response<ApiService.recomendedResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                recomendedGroceriesAdapter = RecommendedProductsAdapter(it.values)
                                recomendedGroceriesRecyclerView.adapter = recomendedGroceriesAdapter
                                checkAllDataFetched()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Sesi Telah Habis",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.recomendedResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchRecommendedMeats() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.recommendedMeats(token)
                .enqueue(object : Callback<ApiService.recomendedResponse> {
                    override fun onResponse(
                        call: Call<ApiService.recomendedResponse>,
                        response: Response<ApiService.recomendedResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                recomendedMeatAdapter = RecommendedProductsAdapter(it.values)
                                recomendedMeatRecyclerView.adapter = recomendedMeatAdapter
                                checkAllDataFetched()
                            }
                        } else {
                            Log.d("ErrorCuy", "onResponse: ${response.code()}")
                            Toast.makeText(
                                context,
                                "Failed to load recommended products",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.recomendedResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private fun fetchRecommendedFruits() {
        val token = context?.let { TokenManager.getToken(it) }
        if (token != null) {
            RetrofitInstance.api.recommendedFruits(token)
                .enqueue(object : Callback<ApiService.recomendedResponse> {
                    override fun onResponse(
                        call: Call<ApiService.recomendedResponse>,
                        response: Response<ApiService.recomendedResponse>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                recomendedFruitAdapter = RecommendedProductsAdapter(it.values)
                                recomendedFruitRecyclerView.adapter = recomendedFruitAdapter
                                checkAllDataFetched()
                            }
                        } else {
                            Log.d("ErrorCuy", "onResponse: ${response.code()}")
                            Toast.makeText(
                                context,
                                "Failed to load recommended products",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiService.recomendedResponse>,
                        t: Throwable
                    ) {
                        Toast.makeText(context, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    private var dataFetchedCount = 0

    private fun checkAllDataFetched() {
        dataFetchedCount++
        if (dataFetchedCount >= 5) { // Adjust this number based on how many API calls you have
            loadingIndicator.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }
    }
}
