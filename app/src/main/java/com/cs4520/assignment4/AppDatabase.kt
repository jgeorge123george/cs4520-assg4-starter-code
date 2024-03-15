package com.cs4520.assignment4

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DBProduct::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun DBProductDao(): DBProductDao
}