package com.example.android.coxcardealer.dealers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.coxcardealer.network.*
import kotlinx.coroutines.*

enum class CoxApiStatus { LOADING, ERROR, DONE }
enum class CoxApiEndpointVersion { NORMAL, CHEAT }

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
  private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


  // Display Dealers list immediately
  init {
    getDealersList(endpointVersion = CoxApiEndpointVersion.NORMAL)
  }

  /**
   * Updates the [Dealer] [List] and [CoxApiStatus] [LiveData]. The Retrofit service
   * returns a Deferred coroutine [CoxApi] call which we await for the result of the transaction.
   * @param endpointVersion represents the mode of api to use, normal or cheat
   */
  private fun getDealersList(endpointVersion: CoxApiEndpointVersion) {
    coroutineScope.launch {
      val getDatasetIdDeferred = CoxApi.retrofitService.getDatasetIdAsync()

      try {
        // Show the loading indicator
        _status.value = CoxApiStatus.LOADING

        //** Get the DatasetId for the unique set of vehicles
        val datasetId = getDatasetIdDeferred.await().datasetId

        when (endpointVersion) {
          // Use normal (slow) Api call
          CoxApiEndpointVersion.NORMAL ->
            processCoxApi(datasetId)

          //** Use the cheat Api call
          CoxApiEndpointVersion.CHEAT ->
            processCheatCoxApi(datasetId)
        }

      } catch (e: Exception) {
        _status.value = CoxApiStatus.ERROR
        _dealers.value = mutableListOf()
        println("CoxApi Access error: $e")
      }
    }
  }

  /**
   * Process the cheat Cox Api - gets endpoint and data structure fully formed
   */
  private suspend fun processCheatCoxApi(datasetId: String?) {
    datasetId?.let {
      // Get the Dealers for this DatasetId
      val listResult = CoxApi.retrofitService.getDealersCheatAsync(it).await()

      // Set the result from CoxApi
      _dealers.value = listResult.dealers
      // Indicate we are finished
      _status.value = CoxApiStatus.DONE
    }
  }

  /**
   * Process the standard Cox Api - Servers simulate high load and long waits to access data.
   */
  private suspend fun processCoxApi(datasetId: String?) {
    datasetId?.let {
      //** Get list of Vehicle Id's for this DatasetId.
      val vehicleIds = CoxApi.retrofitService.getVehicleIdsAsync(datasetId).await()

      //** Start calls for Vehicle info for all the Vehicle Ids, concurrently.
      val vehicleInfoRequests = startVehicleInfoRequest(vehicleIds, datasetId)

      //** Add vehicle's info to vehicles list & start loading dealer's info, concurrently.
      val vehicles = mutableListOf<Vehicle>()
      val dealerIds = mutableSetOf<Int?>()
      val dealerInfoRequests = getVehiclesInfoAndStartDealerInfoReqs(vehicleInfoRequests,
          vehicles, dealerIds, datasetId)

      //** Create list of Dealers with finalized Dealer Info
      val dealers = mutableListOf<Dealer>()
      dealerInfoRequests.forEach { dealers.add(it.await()) }

      //** For the list of VehicleIds, match each Vehicle to it's associated Dealer
      matchVehiclesToDealers(vehicles, dealers)

      // Update UI with result
      _dealers.value = dealers
      // Indicate to UI we are finished
      _status.value = CoxApiStatus.DONE
    }
  }

  /**
   * Get the list of [Vehicle] Id's.
   */
  private fun startVehicleInfoRequest(vehicleIdsApiResult: VehicleIds,
                                      datasetId: String): MutableList<Deferred<Vehicle>> {
    val vehicleInfoRequests = mutableListOf<Deferred<Vehicle>>()
    vehicleIdsApiResult.vehicleIds?.forEach { vehicleId ->
      // Queue up the Requests concurrently
      vehicleInfoRequests.add(CoxApi.retrofitService.getVehicleInfoAsync(datasetId, vehicleId))
    }

    return vehicleInfoRequests
  }

  /**
   * Get the Vehicle info from the Api and concurrently start the Api call to the dealers.
   */
  private suspend fun getVehiclesInfoAndStartDealerInfoReqs(
      vehicleInfoRequests: MutableList<Deferred<Vehicle>>,
      vehicles: MutableList<Vehicle>,
      dealerIds: MutableSet<Int?>,
      datasetId: String): MutableList<Deferred<Dealer>> {

    val dealerInfoRequests = mutableListOf<Deferred<Dealer>>()

    // Sequentially poll the list of vehicleInfoRequests to see which one has completed
    // while we still have vehicle Request active.
    var numActiveVehicleInfoRequests = vehicleInfoRequests.size
    while (numActiveVehicleInfoRequests > 0) {
      numActiveVehicleInfoRequests = vehicleInfoRequests.size

      delay(20) // Relinquish time to OS, don't block the UI.

      // Poll each Vehicle Info Api call to see if it's Completed yet
      for (vehicleInfoCall in vehicleInfoRequests) {

        // Poll to see if vehicleInfo Api request has finished, if so then get the dealerID from
        // vehicleInfo and request the Dealer's Info from Api
        if (vehicleInfoCall.isCompleted || vehicleInfoCall.isCancelled) {
          var vehicle = vehicleInfoCall.await()
          numActiveVehicleInfoRequests--

          // Insert this vehicle into vehicles list, but only it if absent.
          if (!vehicles.contains(vehicle)) {
            vehicles.add(vehicle)
          }

          // If inserting a new dealerId, then concurrently start the request of the dealerId info.
          // Add it to the set of Dealer Id's and start the dealer info request.
          if (!dealerIds.contains(vehicle.dealerId)) {
            dealerIds.add(vehicle.dealerId)
            dealerInfoRequests.add(CoxApi.retrofitService.getDealersInfoAsync(datasetId, vehicle.dealerId))
          }
        }
      }
    }

    return dealerInfoRequests
  }

  /**
   * Match each Vehicle to each Dealer, and add it to Dealers list of vehicles. Mutates dealers.
   */
  private fun matchVehiclesToDealers(vehicles: List<Vehicle>, dealers: MutableList<Dealer>) {
    for (vehicle in vehicles) {
      loop@ for (dealer in dealers) {
        if (dealer.dealerId == vehicle.dealerId) {
          dealer.vehicles?.add(vehicle)
          break@loop // only one dealer per vehicle
        }
      }
    }
  }

  /**
   * When the [ViewModel] is finished, cancel our coroutine [viewModelJob], which tells the
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
