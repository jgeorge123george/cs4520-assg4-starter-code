package com.cs4520.assignment4

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class  DBProduct(
    @PrimaryKey @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: String?,
    @ColumnInfo(name = "productType") val productType: String,
    @ColumnInfo(name = "expiryDate") val expiryDate: String?,
)

