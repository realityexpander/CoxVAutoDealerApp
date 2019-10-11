
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
     * Lazily initialize our [OverviewViewModel].
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
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        // Giving the binding access to the VehiclesViewModel
        binding.viewModel = viewModel

        viewModel.selectedDealer = VehiclesFragmentArgs.fromBundle(arguments!!).selectedDealer

        // Sets the adapter of the RecyclerView with clickHandler lambda that
        // tells the viewModel when our dealer is clicked
        binding.vehiclesList.adapter = VehiclesListAdapter(VehiclesListAdapter.OnClickListener {
          viewModel.displayVehicleDetails(it)
        })

        // Observe the navigateToSelectedDealer LiveData and Navigate when it isn't null
        // After navigating, call displayDealerComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToSelectedVehicle.observe(this, Observer {
          if ( null != it ) {
            // Must find the NavController from the Fragment
            this.findNavController().navigate(VehiclesFragmentDirections.actionShowDetail(it))
            viewModel.displayVehicleDetailsComplete()
          }
        })

        // ** delete
        // Print the current vehicle list
//        viewModel.dealers.observe(this, Observer {
//            it?.forEach { e ->
//                println("Vehicle name: ${e.name}")
//                println("Vehicle id: ${e.vehicleId}")
//            }
//        })

        setHasOptionsMenu(false)
        return binding.root
    }

}
