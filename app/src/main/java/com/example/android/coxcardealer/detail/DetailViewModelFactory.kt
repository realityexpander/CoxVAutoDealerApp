
package com.example.android.coxcardealer.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.coxcardealer.network.Dealer
import com.example.android.coxcardealer.network.Vehicle

/**
 * Detail ViewModel factory that accepts a vehicle & context, supplies VM to the ViewModel.
 */
class DetailViewModelFactory(
    private val vehicle: Vehicle,
    private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(vehicle, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
