package com.example.android.coxcardealer.dealers

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.coxcardealer.databinding.FragmentDealersBinding
import com.example.android.coxcardealer.network.*

/**
 * This fragment shows the the status of the Dealer List web services transaction.
 * Minor refactor here.
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

    // Bind & associate ViewModel with Dealers Fragment
    val binding = FragmentDealersBinding.inflate(inflater)
    binding.lifecycleOwner = this
    binding.viewModel = viewModel

    // Set the adapter of the RecyclerView with clickHandler lambda for Dealer
    binding.dealersList.adapter = DealersListAdapter(
      DealersListAdapter.OnClickListener {
        viewModel.displayVehicles(it)
    })

    // Observe the navigateToSelectedDealer LiveData and Navigate when it isn't null
    // After navigating, displayDealerComplete() will reset for future navigation.
    viewModel.navigateToSelectedDealer.observe(viewLifecycleOwner, Observer { dealer ->
      dealer?.let {
        // Must find the NavController from the Fragment
        this.findNavController().navigate(DealersFragmentDirections.actionShowVehicles(dealer))
        viewModel.displayVehiclesComplete()
      }
    })

    // Init api & client
    setupRetrofitAndOkHttpClient(context)

    return binding.root
  }
}
