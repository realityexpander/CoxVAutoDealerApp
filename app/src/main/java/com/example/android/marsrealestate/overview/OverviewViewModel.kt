/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

    // The MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<CarsApiStatus>()
    val status: LiveData<CarsApiStatus>
        get() = _status

    // ** delete
    // Internally, we use a MutableLiveData, because we will be updating the List of MarsProperty
    // with new values
    private val _properties = MutableLiveData<List<MarsProperty>>()
    val properties: LiveData<List<MarsProperty>>
        get() = _properties

    // Dealers List
    private val _dealers = MutableLiveData<List<Dealer>>()
    val dealers: LiveData<List<Dealer>>
      get() = _dealers

    // ** delete
    // LiveData to handle navigation to the selected property
    private val _navigateToSelectedProperty = MutableLiveData<MarsProperty>()
    val navigateToSelectedProperty: LiveData<MarsProperty>
        get() = _navigateToSelectedProperty

    // LiveData to handle navigation to the selected property
    private val _navigateToSelectedDealer = MutableLiveData<Dealer>()
    val navigateToSelectedDealer: LiveData<Dealer>
      get() = _navigateToSelectedDealer

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()
    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    /**
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */
    init {
      getMarsRealEstateProperties(CarsApiFilter.SHOW_ALL)
      getDealersList()
    }

    /**
     * Gets filtered Mars real estate property information from the Mars API Retrofit service and
     * updates the [Dealer] [List] and [CarsApiStatus] [LiveData]. The Retrofit service
     * returns a coroutine Deferred, which we await to get the result of the transaction.
     * @param filter the [CarsApiFilter] that is sent as part of the web server request
     */
    // ** delete
    private fun getMarsRealEstateProperties(filter: CarsApiFilter) {
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            val getPropertiesDeferred = MarsApi.retrofitService.getProperties(filter.value)
            try {
                _status.value = CarsApiStatus.LOADING
                // this will run on a thread managed by Retrofit
                val listResult = getPropertiesDeferred.await()
                _status.value = CarsApiStatus.DONE
                _properties.value = listResult
            } catch (e: Exception) {
                _status.value = CarsApiStatus.ERROR
                _properties.value = ArrayList()
            }
        }
    }

  private fun getDealersList() {
    coroutineScope.launch {
      // Get the Deferred object for our Retrofit request
      val getDealersDeferred = CarsApi.retrofitService2.getDealersAsync()
      try {
        _status.value = CarsApiStatus.LOADING
        // this will run on a thread managed by Retrofit
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
     * Updates the data set filter for the web services by querying the data with the new filter
     * by calling [getMarsRealEstateProperties]
     * @param filter the [CarsApiFilter] that is sent as part of the web server request
     */
    fun updateFilter(filter: CarsApiFilter) {
        getMarsRealEstateProperties(filter)
    }

    /**
     * When the property is clicked, set the [_navigateToSelectedProperty] [MutableLiveData]
     * @param marsProperty The [MarsProperty] that was clicked on.
     */
    // ** delete
    fun displayPropertyDetails(marsProperty: MarsProperty) {
        _navigateToSelectedProperty.value = marsProperty
    }

    fun displayDealerDetails(dealer: Dealer) {
      _navigateToSelectedDealer.value = dealer
    }

    /**
     * After the navigation has taken place, make sure navigateToSelectedProperty is set to null
     */
    // ** delete
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
    fun displayDealerDetailsComplete() {
      _navigateToSelectedProperty.value = null
    }
}
