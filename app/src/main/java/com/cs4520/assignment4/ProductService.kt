package com.cs4520.assignment4

import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {
    @GET("/prod/")
    suspend fun getProducts(@Query("page") page: Int): List<ProductResponse>

//      @GET("/typicode/demo/posts")
//      suspend fun getProducts(): List<Product>

}