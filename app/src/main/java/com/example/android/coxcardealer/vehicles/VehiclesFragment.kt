
package com.example.android.coxcardealer.vehicles

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.coxcardealer.databinding.FragmentVehiclesBinding

/**
 * This fragment shows the the selected Vehicle of the selected Dealer.
 */
class VehiclesFragment : Fragment() {

    /**
     * Lazily initialize our [VehiclesViewModel].
     */
    private val viewModel: VehiclesViewModel by lazy {
        ViewModelProviders.of(this).get(VehiclesViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the VehiclesFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVehiclesBinding.inflate(inflater)
        binding.lifecycleOwner = this
        // Giving the binding access to VehiclesViewModel
        binding.viewModel = viewModel
        // Retrieve the argument & set the selected dealer
        viewModel.setSelectedDealer(VehiclesFragmentArgs.fromBundle(arguments!!).selectedDealer)

        // Set the adapter of the RecyclerView with the clickHandler lambda to navigate
        binding.vehiclesList.adapter = VehiclesListAdapter(VehiclesListAdapter.OnClickListener {
          viewModel.displayDetails(it)
        })

        // Navigation to show selected vehicle detail
        viewModel.navigateToDetails.observe(this, Observer {
          if ( null != it ) {
            this.findNavController().navigate(VehiclesFragmentDirections.actionShowDetail(it))
            viewModel.displayDetailsComplete()
          }
        })

        return binding.root
    }

}
