package com.example.android.coxcardealer.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.coxcardealer.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.android.coxcardealer.network.datasetId as networkDatasetId

enum class CarsApiStatus { LOADING, ERROR, DONE }

/**
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


    // Display dealers list immediately
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

        networkDatasetId?.let {
          // Get the Dealers for this DatasetId
          val listResult = getDealersCheatUsingDatasetIdAsync().await()
          _status.value = CarsApiStatus.DONE

          // Set the result from CarsApi
          _dealers.value = listResult.dealers
        }

      } catch (e: Exception) {
        _status.value = CarsApiStatus.ERROR
        _dealers.value = ArrayList()
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
