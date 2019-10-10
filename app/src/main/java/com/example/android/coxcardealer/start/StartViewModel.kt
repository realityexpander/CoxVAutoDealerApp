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

package com.example.android.coxcardealer.start

import android.app.Application
import androidx.lifecycle.*

class StartViewModel(/*marsProperty: MarsProperty,*/
                     app: Application) : AndroidViewModel(app) {

    // The internal MutableLiveData for the selected property
    private val _navigateToOverview = MutableLiveData<Boolean?>()
    val navigateToOverview: LiveData<Boolean?>
        get() = _navigateToOverview

    // Initialize the _selectedProperty MutableLiveData
    init {
        _navigateToOverview.value = false
    }


    fun displayOverview() {
        _navigateToOverview.value = true
    }

    fun displayOverviewComplete() {
        _navigateToOverview.value = false
    }


}

