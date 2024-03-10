package com.cs4520.assignment4

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class ProductListFragment : Fragment() {

    private lateinit var adapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchProductData()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(ArrayList())
        recyclerView = view?.findViewById(R.id.recycler_view) ?: return
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchProductData() {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Set log level to BODY for full response logging
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://kgtttq6tg9.execute-api.us-east-2.amazonaws.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        val service = retrofit.create(ProductService::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val products = service.getProducts(3)
                println(products)
                println(products)

                val convertedProducts: List<Product> = products.map { response ->
                    when (response.type) {
                        ProductType.Equipment -> {
                            val equipmentExpiryDate = response.expiryDate as? LocalDate
                            Product.Equipment(
                                response.name,
                                response.price,
                                response.type,
                                equipmentExpiryDate
                            )
                        }
                        ProductType.Food -> {
                            val foodExpiryDate = response.expiryDate as? LocalDate
                            Product.Food(
                                response.name,
                                response.price,
                                response.type,
                                foodExpiryDate
                            )
                        }
                    }
                }


//                val products = service.getProducts(3) // Fetch products from page 3
//                val products = service.getProducts()
                adapter.updateData(convertedProducts)
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error
            }
        }
    }
}