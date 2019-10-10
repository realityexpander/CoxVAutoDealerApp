
package com.example.android.marsrealestate.overview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.marsrealestate.R
import com.example.android.marsrealestate.databinding.FragmentOverviewBinding
import com.example.android.marsrealestate.network.CarsApiFilter

/**
 * This fragment shows the the status of the Dealer List web services transaction.
 */
class OverviewFragment : Fragment() {

    /**
     * Lazily initialize our [OverviewViewModel].
     */
    private val viewModel: OverviewViewModel by lazy {
        ViewModelProviders.of(this).get(OverviewViewModel::class.java)
    }

    /**
     * Inflates the layout with Data Binding, sets its lifecycle owner to the OverviewFragment
     * to enable Data Binding to observe LiveData, and sets up the RecyclerView with an adapter.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentOverviewBinding.inflate(inflater)
        //val binding = GridViewItemBinding.inflate(inflater) // ** delete

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Sets the adapter of the photosGrid RecyclerView with clickHandler lambda that
        // tells the viewModel when our property is clicked
        // ** delete
//        binding.photosGrid.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
//            viewModel.displayPropertyDetails(it)
//        })
        binding.dealersList.adapter = PhotoGridAdapter(PhotoGridAdapter.OnClickListener {
          viewModel.displayDealerDetails(it)
        })

        // Observe the navigateToSelectedProperty LiveData and Navigate when it isn't null
        // After navigating, call displayPropertyDetailsComplete() so that the ViewModel is ready
        // for another navigation event.
        // ** delete
//        viewModel.navigateToSelectedProperty.observe(this, Observer {
//            if ( null != it ) {
//                // Must find the NavController from the Fragment
//                this.findNavController().navigate(OverviewFragmentDirections.actionShowDetail(it))
//                // Tell the ViewModel we've made the navigate call to prevent multiple navigation
//                viewModel.displayPropertyDetailsComplete()
//            }
//        })
        viewModel.navigateToSelectedDealer.observe(this, Observer {
          if ( null != it ) {
            // Must find the NavController from the Fragment
            this.findNavController().navigate(OverviewFragmentDirections.actionShowDetail(it))
            viewModel.displayDealerDetailsComplete()
          }
        })

        viewModel.dealers.observe(this, Observer {
            it?.forEach { e ->
                println("Dealer name: ${e.name}")
                println("Dealer id: ${e.dealerId}")
                println("Dealer vehicles: ${e.vehicles?.size}")
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    /**
     * Inflates the overflow menu that contains filtering options.
     */
    // ** delete?
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Updates the filter in the [OverviewViewModel] when the menu items are selected from the
     * overflow menu.
     */
    // ** delete?
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
                when (item.itemId) {
                    R.id.show_rent_menu -> CarsApiFilter.SHOW_RENT
                    R.id.show_buy_menu -> CarsApiFilter.SHOW_BUY
                    else -> CarsApiFilter.SHOW_ALL
                }
        )
        return true
    }
}
