package com.cs4520.assignment4

import android.app.usage.UsageEvents
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.fragment.app.Fragment


class LoginViewModel : ViewModel(){

    private val _navigateToDestination = MutableLiveData<Boolean>()
    val navigateToDestination: LiveData<Boolean> get() = _navigateToDestination


    fun performAuth(fragment: Fragment, username: String, password: String) {
        if (Auth.authenticate(username, password)) {
            Toast.makeText(fragment.requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
            _navigateToDestination.value = true
        } else {
            Toast.makeText(fragment.requireContext(), "Login failed", Toast.LENGTH_SHORT).show()
            _navigateToDestination.value = false
        }
    }

    fun onNavigationComplete() {
        _navigateToDestination.value = false
    }
}