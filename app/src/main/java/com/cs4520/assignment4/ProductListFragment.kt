package com.cs4520.assignment4

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class ProductListFragment : Fragment() {

    private lateinit var adapter: ProductAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var currentPage = 1
    private val maxPages = 6
    private val fetchedProductNames = mutableSetOf<String>()


    private var isLoading = false


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

        progressBar = view?.findViewById(R.id.progress_bar)!!

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (dy > 0 && !isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                    currentPage++
                    fetchProductData()
                }
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchProductData() {
        isLoading = true
        progressBar.visibility = View.VISIBLE

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
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

        val dbProductsList = mutableListOf<DBProduct>()


        val db = Room.databaseBuilder(
            requireContext().applicationContext,
            AppDatabase::class.java, "products"
        ).build()

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                if (currentPage <= maxPages) {
                    val products = service.getProducts(currentPage)
                    println(products)

                    var convertedProducts: List<Product> = products
                        .filterNot { fetchedProductNames.contains(it.name) }
                        .filter { (it.type == ProductType.Food && it.expiryDate != null)
                                || it.type == ProductType.Equipment  }
                        .map { response ->
                            fetchedProductNames.add(response.name.toString())
                            when (response.type) {
                                ProductType.Equipment -> {
                                    val dbProduct = DBProduct(
                                        name = response.name.toString(),
                                        price = response.price.toString(),
                                        productType = response.type.toString(),
                                        expiryDate = ""
                                    )
                                    dbProductsList.add(dbProduct)
                                    Product.Equipment(
                                        response.name,
                                        response.price,
                                        response.type
                                    )
                                }
                                ProductType.Food -> {
                                    val dbProduct = DBProduct(
                                        name = response.name.toString(),
                                        price = response.price.toString(),
                                        productType = response.type.toString(),
                                        expiryDate = response.expiryDate.toString()
                                    )
                                    dbProductsList.add(dbProduct)
                                    Product.Food(
                                        response.name,
                                        response.price,
                                        response.type,
                                        LocalDate.parse(response.expiryDate.toString())
                                    )
                                }
                            }
                        }

                    convertedProducts = productsFromDBIfNeeded(products, db, convertedProducts)


                    if (products.isEmpty()) {
                        showToast("No products available")
                        return@launch
                    }

                    val dbProductsArray = dbProductsList.toTypedArray() // Convert to array

                    withContext(Dispatchers.IO) {
                        db.DBProductDao().deleteAll()
                        db.DBProductDao().insertAll(*dbProductsArray)
                    }

                    adapter.updateData(convertedProducts)
                }
            } catch (e: Exception) {
                val err = e.message
                isLoading = false
                showToast("Error fetching products")

                // Load products from DB in case of an HTTP error
                val convertedProducts = productsFromDBIfNeeded(listOf(), db, listOf())
                if (convertedProducts.isEmpty()) {
                    showToast("No products available in Local database")
                } else {
                    adapter.updateData(convertedProducts)
                }
            } finally {
                progressBar.visibility = View.GONE
                isLoading = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun productsFromDBIfNeeded(
        products: List<ProductResponse>,
        db: AppDatabase,
        convertedProducts: List<Product>
    ): List<Product> {
        var convertedProducts1 = convertedProducts
        if (products.isEmpty()) {
            showToast("No products got from API, trying to load from local DB")
            withContext(Dispatchers.IO) {
                val dbProductsListFromDb = db.DBProductDao().getAll()
                if (dbProductsListFromDb.isEmpty()) {
                    showToast("No products available in Local database")
                } else {
                    convertedProducts1 = dbProductsListFromDb.map { dbProduct ->
                        when (dbProduct.productType) {
                            "Equipment" -> {
                                Product.Equipment(
                                    dbProduct.name,
                                    dbProduct.price,
                                    ProductType.Equipment
                                )
                            }

                            "Food" -> {
                                Product.Food(
                                    dbProduct.name,
                                    dbProduct.price,
                                    ProductType.Food,
                                    LocalDate.parse(dbProduct.expiryDate)
                                )
                            }

                            else -> throw IllegalArgumentException("Unknown product type: ${dbProduct.productType}")
                        }
                    }
                }
            }
        }
        return convertedProducts1
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}