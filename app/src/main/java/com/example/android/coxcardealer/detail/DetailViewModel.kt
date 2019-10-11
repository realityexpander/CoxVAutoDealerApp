/*
 *  Copyright 2019, The Android Open Source Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.android.coxcardealer.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.android.coxcardealer.network.MarsProperty
import com.example.android.coxcardealer.network.Dealer
import com.example.android.coxcardealer.network.Vehicle

/**
 *  The [ViewModel] associated with the [DetailFragment], containing information about the selected
 *  [Vehicle].
 */
class DetailViewModel(vehicle: Vehicle,
                      app: Application) : AndroidViewModel(app) {

    // The selected vehicle
  private val _selectedVehicle = MutableLiveData<Vehicle>()
    val selectedVehicle: LiveData<Vehicle>
        get() = _selectedVehicle

    // Initialize the _selectedVehicle MutableLiveData
    init {
      _selectedVehicle.value = vehicle
    }

}

