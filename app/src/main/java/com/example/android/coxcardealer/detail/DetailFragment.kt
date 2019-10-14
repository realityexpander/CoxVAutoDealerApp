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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.android.coxcardealer.databinding.FragmentDetailBinding

/**
 * This [Fragment] shows the detailed information about a selected [Vehicle] of a selected
 * [Dealer]. It sets this information in the [DetailViewModel].
 *
 * selectedVehicle from SafeArgs contains the Vehicle
 */
class DetailFragment : Fragment() {

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    // Bind & associate ViewModel with Details Fragment
    val application = requireNotNull(activity).application
    val binding = FragmentDetailBinding.inflate(inflater)
    binding.lifecycleOwner = this
    val vehicle = DetailFragmentArgs.fromBundle(arguments!!).selectedVehicle

    val viewModelFactory = DetailViewModelFactory(vehicle, application)
    binding.viewModel = ViewModelProviders.of(
        this, viewModelFactory).get(DetailViewModel::class.java)

    return binding.root
  }
}