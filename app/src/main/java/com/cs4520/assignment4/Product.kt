package com.cs4520.assignment4

import java.time.LocalDate



sealed class Product(
    val name: Any?,
    val price: Any?,
    val type: ProductType,
    val expiryDate: LocalDate? = null
) {
    data class Equipment(
        val equipmentName: Any?,
        val equipmentPrice: Any?,
        val prodType: ProductType,
        val equipmentExpiryDate: LocalDate? = null
    ) : Product(equipmentName, equipmentPrice, ProductType.Equipment, equipmentExpiryDate)

    data class Food(
        val foodName: Any?,
        val foodPrice: Any?,
        val prodType: ProductType,
        val foodExpiryDate: LocalDate? = null
    ) : Product(foodName, foodPrice, ProductType.Food, foodExpiryDate)


}


enum class ProductType {
    Equipment,
    Food
}

