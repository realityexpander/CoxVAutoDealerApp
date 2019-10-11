package com.example.android.coxcardealer.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.coxcardealer.network.*
import kotlinx.coroutines.*
import com.example.android.coxcardealer.network.datasetId as networkDatasetId

enum class CarsApiStatus { LOADING, ERROR, DONE }

/**
 * Show the list of [ Dealers].
 *
 * The [ViewModel] that is attached to the [VehiclesFragment].
 */
class OverviewViewModel : ViewModel() {

    // Status of the most recent request from API
    private val _status = MutableLiveData<CarsApiStatus>()
    val status: LiveData<CarsApiStatus>
        get() = _status

    // Dealers List
    private val _dealers = MutableLiveData<List<Dealer>>()
    val dealers: LiveData<List<Dealer>>
      get() = _dealers

    // Handle navigation to the selected dealer
    private val _navigateToVehicles = MutableLiveData<Dealer>()
    val navigateToSelectedDealer: LiveData<Dealer>
      get() = _navigateToVehicles

    // Coroutine scope for API call uses a Job to be able to cancel when needed
    private var viewModelJob = Job()
    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    // Display Dealers list immediately
    init {
      getDealersList()
    }

  /**
   * Updates the [Dealer] [List] and [CarsApiStatus] [LiveData]. The Retrofit service
   * returns a Deferred coroutine [CarsApi] call which we await for the result of the transaction.
   */
  private fun getDealersList() {
    coroutineScope.launch {
      // Get the Deferred object for our Retrofit request

      val getDatasetIdDeferred = CarsApi.retrofitService.getDatasetIdAsync()
      try {
        _status.value = CarsApiStatus.LOADING

        // Get the DatasetId
        networkDatasetId = getDatasetIdDeferred.await().datasetId

        /**
         *  Get the Dealer info for all the vehicles in the DatasetId
          */
        networkDatasetId?.let { datasetId ->
          // Get the list of Vehicles for this DatasetId
          val vehicleIdsApiResult = CarsApi.retrofitService.getVehiclesAsync(datasetId).await()

          /**
           * Get Vehicle info for all of the Vehicle Ids, in parallel
           */
          var vehicles = ArrayList<Vehicle>()
          val vehicleInfoCalls = mutableListOf<Deferred<Vehicle>>()
          vehicleIdsApiResult.vehicleIds?.forEach { vehicleId ->
            // Queue up the calls in parallel
            vehicleInfoCalls.add(CarsApi.retrofitService.getVehicleInfoAsync(datasetId, vehicleId))
          }
          // Add the vehicle's info to the vehicles list
          vehicleInfoCalls.forEach { vehicleInfoCall ->
            vehicles.add(vehicleInfoCall.await())
          }

          /**
           *  Determine the unique Set of Dealers based on each vehicle's dealerId
           */
          var dealerSet = mutableSetOf<Int?>()
          vehicles.forEach { vehicle ->
            dealerSet.add(vehicle.dealerId)
          }

          /**
           *  For the Set of Dealers, get info from Api for each Dealer, in parallel
           */
          var dealers = mutableListOf<Dealer>()
          val dealerInfoCalls = mutableListOf<Deferred<Dealer>>()
          // Queue up the calls in parallel
          dealerSet.forEach { dealerId ->
            dealerInfoCalls.add(CarsApi.retrofitService.getDealersInfoAsync(datasetId, dealerId) )
          }
          // Add the DealerInfo to Dealers Set
          dealerInfoCalls.forEach { dealerInfoCall ->
            dealers.add(dealerInfoCall.await())
          }

          /**
           *  For the set of Dealers, add the Vehicle that matches the dealer
           */
          for(vehicle in vehicles) {
            for(dealer in dealers) {
              if(dealer.dealerId == vehicle.dealerId) {
                (dealer.vehicles as ArrayList<Vehicle>).add(vehicle) // ?? fixme why must use this way
              }
            }
          }

          // Assign the result to LiveData
          _dealers.value = dealers

          // Indicate we are finished
          _status.value = CarsApiStatus.DONE
        }

//        // Get via cheat
//        networkDatasetId?.let {
//          // Get the Dealers for this DatasetId
//          val listResult = getDealersCheatUsingDatasetIdAsync().await()
//          _status.value = CarsApiStatus.DONE
//
//          // Set the result from CarsApi
//          _dealers.value = listResult.dealers
//        }

      } catch (e: Exception) {
        _status.value = CarsApiStatus.ERROR
        _dealers.value = ArrayList()
        println("CarsApi Access error: $e")
      }
    }
  }

    /**
     * When the [ViewModel] is finished, we cancel our coroutine [viewModelJob], which tells the
     * Retrofit service to stop.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    /**
     * When the property is clicked, set the [_navigateToVehicles] [MutableLiveData]
     * @param dealer is The [Dealer] that was clicked on.
     */
    fun displayVehicles(dealer: Dealer) {
      _navigateToVehicles.value = dealer
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayVehiclesComplete() {
      _navigateToVehicles.value = null
    }
}
