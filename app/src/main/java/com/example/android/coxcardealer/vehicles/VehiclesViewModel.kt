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
    val selectedDealer: LiveData<Dealer>
      get() = _selectedDealer

    // Handle navigation to the selected vehicle
    private val _navigateToSelectedVehicle = MutableLiveData<Vehicle>()
    val navigateToSelectedVehicle: LiveData<Vehicle>
      get() = _navigateToSelectedVehicle


    // Display vehicles list immediately.
    init {
      //getVehiclesList()
    }

  /**
   * Gets the vehicles from the dealer [Dealer] [Vehicle] [List]
   */
    fun getVehiclesList() {
      // Map the list of dealer's vehicles to an array for the RecyclerView
//      _vehicles.value = ArrayList()
      _vehicles.value = selectedDealer.value?.vehicles
//    // ** fixme - get dealer & map array vehicles to the listView
//      selectedDealer?.value?.vehicles?.map {
//        (_vehicles.value as ArrayList<Vehicle>).add(it)
//      }
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedVehicle] [MutableLiveData]
     * @param vehicle is The [Vehicle] that was clicked on.
     */
    // fixme - called from adapter
    fun displayVehicleDetails(vehicle: Vehicle) {
      _navigateToSelectedVehicle.value = vehicle
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayVehicleDetailsComplete() {
      _navigateToSelectedVehicle.value = null
    }

    fun setSelectedDealer(dealer: Dealer) {
      _selectedDealer.value = dealer
      getVehiclesList()
    }
}
