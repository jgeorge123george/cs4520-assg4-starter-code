package com.cs4520.assignment4

object Auth {

    fun authenticate(username: String, password: String): Boolean {
        return username == "admin" && password == "admin"
    }

}