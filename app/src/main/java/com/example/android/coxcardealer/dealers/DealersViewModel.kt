package com.example.android.coxcardealer.dealers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.coxcardealer.network.*
import kotlinx.coroutines.*
import com.example.android.coxcardealer.network.datasetId as networkDatasetId

enum class CoxApiStatus { LOADING, ERROR, DONE }
enum class CoxApiEndpointFormat { NORMAL, CHEAT }

/**
 * Show the list of [ Dealers].
 *
 * The [ViewModel] that is attached to the [VehiclesFragment].
 */
class DealersViewModel : ViewModel() {

    // Status of the most recent request from API
    private val _status = MutableLiveData<CoxApiStatus>()
    val status: LiveData<CoxApiStatus>
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
      getDealersList(viaEndpoint = CoxApiEndpointFormat.NORMAL)
    }

  /**
   * Updates the [Dealer] [List] and [CoxApiStatus] [LiveData]. The Retrofit service
   * returns a Deferred coroutine [CarsApi] call which we await for the result of the transaction.
   * @param viaEndpoint represents the mode of api to use, normal or cheat
   */
  private fun getDealersList(viaEndpoint: CoxApiEndpointFormat) {
    coroutineScope.launch {
      val getDatasetIdDeferred = CarsApi.retrofitService.getDatasetIdAsync()

      try {
        // Show the loading indicator
        _status.value = CoxApiStatus.LOADING

        //** Get the DatasetId
        networkDatasetId = getDatasetIdDeferred.await().datasetId

        //** Choose Normal or Cheat endpoint
        when (viaEndpoint) {

          CoxApiEndpointFormat.NORMAL ->
            //** Get the Dealer info for all the vehicles in the DatasetId
            networkDatasetId?.let { datasetId ->
              //** Get the list of Vehicle Id's for this DatasetId.
              val vehicleIds = CarsApi.retrofitService.getVehiclesAsync(datasetId).await()

              //** Start calls for Vehicle info for all the Vehicle Ids, concurrently.
              val vehicleInfoRequest = startVehicleInfoRequest(vehicleIds, datasetId)

              //** Add the vehicle's info to the vehicles list & start loading
              // dealer's info, concurrently.
              val vehicles = mutableListOf<Vehicle>()
              val dealerIdsSet = mutableSetOf<Int?>()
              val dealerInfoRequest = getVehiclesAndStartDealerInfoRequests(vehicleInfoRequest, vehicles, dealerIdsSet, datasetId)

              //** Create list of Dealers with full Dealer Info
              val dealers = mutableListOf<Dealer>()
              dealerInfoRequest.forEach { dealers.add(it.await()) }

              //** For the set of Dealers, match the Vehicle to the Dealer
              matchVehiclesToDealers(vehicles, dealers)

              // Assign the result to LiveData
              _dealers.value = dealers
              // Indicate we are finished
              _status.value = CoxApiStatus.DONE
            }

          CoxApiEndpointFormat.CHEAT ->
            // ** Get via cheat Api
            networkDatasetId?.let {
              // Get the Dealers for this DatasetId
              val listResult = CarsApi.retrofitService.getDealersCheatAsync(it).await()

              // Set the result from CarsApi
              _dealers.value = listResult.dealers
              // Indicate we are finished
              _status.value = CoxApiStatus.DONE
            }
        }

      } catch (e: Exception) {
        _status.value = CoxApiStatus.ERROR
        _dealers.value = mutableListOf()
        println("CarsApi Access error: $e")
      }
    }
  }

  /**
   * Match each Vehicle to each Dealer that it is associated with, and add it to Dealers
   * list of vehicles. Mutates dealers
   */
  private fun matchVehiclesToDealers(vehicles: List<Vehicle>, dealers: MutableList<Dealer>) {
    for (vehicle in vehicles) {
      loop@ for (dealer in dealers) {
        if (dealer.dealerId == vehicle.dealerId) {
          (dealer.vehicles as MutableList<Vehicle>).add(vehicle) // ** investigate: why must call this way
          break@loop
        }
      }
    }
  }

  /**
   * Get the Vehicle info from the Api and start the Api call to the dealers, concurrently.
   */
  private suspend fun getVehiclesAndStartDealerInfoRequests(
      vehicleInfoRequest: MutableList<Deferred<Vehicle>>,
      vehicles: MutableList<Vehicle>,
      dealerIdsSet: MutableSet<Int?>,
      datasetId: String): MutableList<Deferred<Dealer>> {

    val dealerInfoRequest = mutableListOf<Deferred<Dealer>>()
    var numActiveVehicleInfoRequest = vehicleInfoRequest.size

    // Sequentially poll the list of vehicleInfoRequest to see which one has completed
    // while we still have vehicle Request active.
    while (numActiveVehicleInfoRequest > 0) {
      numActiveVehicleInfoRequest = vehicleInfoRequest.size

      // Poll each Vehicle Info Api call to see if it's Completed yet
      for (vehicleInfoCall in vehicleInfoRequest) {

        // Poll to see if vehicleInfo Api request has finished, if so then get the dealerID from
        // vehicleInfo and request the Dealer's Info from Api
        if (vehicleInfoCall.isCompleted || vehicleInfoCall.isCancelled) {
          var vehicle = vehicleInfoCall.await()
          numActiveVehicleInfoRequest--

          // Check if we don't have this vehicle in our vehicles list yet, and add it if absent.
          if (!vehicles.contains(vehicle)) {
            vehicles.add(vehicle)
          }

          // Is the dealerId of this vehicle not in the set of known dealers?
          //  Add it to the set of Dealer Id's and start the dealer info request.
          if (!dealerIdsSet.contains(vehicle.dealerId)) {
            dealerIdsSet.add(vehicle.dealerId)
            dealerInfoRequest.add(CarsApi.retrofitService.getDealersInfoAsync(datasetId, vehicle.dealerId))
          }
        }
      }
    }
    return dealerInfoRequest
  }

  private fun startVehicleInfoRequest(vehicleIdsApiResult: Vehicles,
                                    datasetId: String): MutableList<Deferred<Vehicle>> {
    val vehicleInfoRequest = mutableListOf<Deferred<Vehicle>>()
    vehicleIdsApiResult.vehicleIds?.forEach { vehicleId ->
      // Queue up the Request in parallel
      vehicleInfoRequest.add(CarsApi.retrofitService.getVehicleInfoAsync(datasetId, vehicleId))
    }
    return vehicleInfoRequest
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
