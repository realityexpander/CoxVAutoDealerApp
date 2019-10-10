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

package com.example.android.marsrealestate.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.network.Dealer

/**
 *  The [ViewModel] associated with the [StartFragment], containing information about the selected
 *  [MarsProperty].
 */
class DetailViewModel(
                      dealer: Dealer,
                      app: Application) : AndroidViewModel(app) {

    // The selected dealer
  private val _selectedDealer = MutableLiveData<Dealer>()
    val selectedDealer: LiveData<Dealer>
        get() = _selectedDealer

    // Initialize the _selectedProperty MutableLiveData
    init {
      _selectedDealer.value = dealer
    }

}

