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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.ViewModelProviders.*
import androidx.navigation.fragment.findNavController
import com.example.android.coxcardealer.databinding.FragmentStartBinding

/**
 * Starting view
 */
class StartFragment : Fragment() {

    /**
     * Lazily init our [StartViewModel].
     */
    private val viewModel: StartViewModel by lazy {
        ViewModelProviders.of(this).get(StartViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val application = requireNotNull(activity).application
        val binding = FragmentStartBinding.inflate(inflater)
        binding.lifecycleOwner = this
        val viewModelFactory = StartViewModelFactory(application)
        binding.viewModel = of(
                this, viewModelFactory).get(StartViewModel::class.java)

        // Navigate to Overview screen
        viewModel.navigateToOverview.observe(this, Observer {
            if ( it != false ) {
                this.findNavController().navigate(StartFragmentDirections.actionShowOverview())
                viewModel.displayOverviewComplete()
            }
        })

        return binding.root
    }

}