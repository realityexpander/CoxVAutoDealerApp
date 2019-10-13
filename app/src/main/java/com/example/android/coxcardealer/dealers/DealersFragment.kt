
package com.example.android.coxcardealer.dealers

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.coxcardealer.databinding.FragmentDealersBinding

/**
 * This fragment shows the the status of the Dealer List web services transaction.
 */
class DealersFragment : Fragment() {

    /**
     * Lazy init our [DealersViewModel]
     */
    private val viewModel: DealersViewModel by lazy {
        ViewModelProviders.of(this).get(DealersViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the VehiclesFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentDealersBinding.inflate(inflater)
        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this
        // Giving the binding access to the DealersViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the RecyclerView with clickHandler lambda that
        // tells the viewModel when our dealer is clicked
        binding.dealersList.adapter = DealersListAdapter(DealersListAdapter.OnClickListener {
          viewModel.displayVehicles(it)
        })

        // Observe the navigateToSelectedDealer LiveData and Navigate when it isn't null
        // After navigating, call displayDealerComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToSelectedDealer.observe(this, Observer {
          if ( null != it ) {
            // Must find the NavController from the Fragment
            this.findNavController().navigate(DealersFragmentDirections.actionShowVehicles(it))
            viewModel.displayVehiclesComplete()
          }
        })

        return binding.root
    }
}
