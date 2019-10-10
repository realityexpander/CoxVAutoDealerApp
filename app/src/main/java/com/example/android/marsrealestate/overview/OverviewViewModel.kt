package com.example.android.marsrealestate.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.marsrealestate.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

enum class CarsApiStatus { LOADING, ERROR, DONE }

/**
 * The [ViewModel] that is attached to the [OverviewFragment].
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
    private val _navigateToSelectedDealer = MutableLiveData<Dealer>()
    val navigateToSelectedDealer: LiveData<Dealer>
      get() = _navigateToSelectedDealer

    // Coroutine scope uses a Job to be able to cancel when needed
    private var viewModelJob = Job()
    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    // Display status immediately.
    init {
      getDealersList()
    }

  /**
   * Gets filtered Mars real estate property information from the Mars API Retrofit service and
   * updates the [Dealer] [List] and [CarsApiStatus] [LiveData]. The Retrofit service
   * returns a coroutine Deferred, which we await to get the result of the transaction.
   * @param filter the [CarsApiFilter] that is sent as part of the web server request
   */
  private fun getDealersList() {
    coroutineScope.launch {
      // Get the Deferred object for our Retrofit request
      val getDealersDeferred = CarsApi.retrofitService2.getDealersAsync()
      try {
        _status.value = CarsApiStatus.LOADING

        val listResult = getDealersDeferred.await()
        _status.value = CarsApiStatus.DONE

        // Map the retrieved dealers object to an array for the gridview
        _dealers.value = ArrayList()
        listResult.dealers?.map {
          (_dealers.value as ArrayList<Dealer>).add(it)
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
     * When the property is clicked, set the [_navigateToSelectedDealer] [MutableLiveData]
     * @param dealer is The [Dealer] that was clicked on.
     */
    fun displayDealerDetails(dealer: Dealer) {
      _navigateToSelectedDealer.value = dealer
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    fun displayDealerDetailsComplete() {
      _navigateToSelectedDealer.value = null
    }
}
