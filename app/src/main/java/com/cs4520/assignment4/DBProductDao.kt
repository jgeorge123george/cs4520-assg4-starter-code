package com.cs4520.assignment4

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DBProductDao {

    @Query("SELECT * FROM DBProduct")
    fun getAll(): List<DBProduct>


    @Insert
    fun insertAll(vararg products: DBProduct)

    @Delete
    fun delete(products: Array<DBProduct>)

    @Query("DELETE FROM DBProduct")
    fun deleteAll()

}