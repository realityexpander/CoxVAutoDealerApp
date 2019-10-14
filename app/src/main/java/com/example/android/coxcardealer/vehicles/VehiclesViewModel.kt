package com.example.android.coxcardealer.vehicles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.coxcardealer.network.*

/**
 * The [ViewModel] that is attached to the [VehiclesFragment].
 */
class VehiclesViewModel : ViewModel() {

    // Vehicles List
    private val _vehicles = MutableLiveData<List<Vehicle>>()
    val vehicles: LiveData<List<Vehicle>>
      get() = _vehicles

    // Selected Dealer
    private val _selectedDealer = MutableLiveData<Dealer>()
    private val selectedDealer: LiveData<Dealer>
      get() = _selectedDealer

    // String for UI
    val selectedDealerStr
      get() = "Vehicles at ${selectedDealer.value?.name}"

    // Handle navigation to the selected vehicle
    private val _navigateToDetails = MutableLiveData<Vehicle>()
    val navigateToDetails: LiveData<Vehicle>
      get() = _navigateToDetails

    /**
     * Sets the vehicles from the dealer [Dealer] to the [Vehicle] [List]
     */
    private fun setVehiclesList() {
      _vehicles.value = selectedDealer.value?.vehicles
    }

    /**
     * When the property is clicked, set the [_navigateToDetails] [MutableLiveData]
     * @param vehicle is The [Vehicle] that was clicked on.
     */
    fun displayDetails(vehicle: Vehicle) {
      _navigateToDetails.value = vehicle
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayDetailsComplete() {
      _navigateToDetails.value = null
    }

    fun setSelectedDealer(dealer: Dealer) {
      _selectedDealer.value = dealer
      setVehiclesList()
    }
}
