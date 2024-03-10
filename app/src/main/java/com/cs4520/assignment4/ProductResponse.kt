package com.cs4520.assignment4

import java.time.LocalDate

data class ProductResponse(
    val name: Any?,
    val price: Any?,
    val type: ProductType,
    val expiryDate: Any?
)