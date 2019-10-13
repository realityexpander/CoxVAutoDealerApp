
package com.example.android.coxcardealer.dealers

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.android.coxcardealer.databinding.FragmentDealersBinding
import com.example.android.coxcardealer.network.*
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File

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
        // After navigating, call displayDealerComplete() to setup ViewModel to be ready
        // for another navigation event.
        viewModel.navigateToSelectedDealer.observe(this, Observer {
          if ( null != it ) {
            // Must find the NavController from the Fragment
            this.findNavController().navigate(DealersFragmentDirections.actionShowVehicles(it))
            viewModel.displayVehiclesComplete()
          }
        })

        setupRetrofitClient(context)
        // Move to CarsApiSerivice
//        // <<<<<<<<<<<<<<<<<
//        private cacheDir = File(context?.cacheDir?.path + "/cox_cache" )
//        private client = OkHttpClient.Builder()
//            .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
//            .dispatcher(dispatcher)
//            .connectionPool(pool)
//            .cache(Cache(
//                cacheDir,
//                10L * 1024L * 1024L // 1 MiB
//            ))
//            .addInterceptor { chain ->
//              var request = chain.request()
//              request = if (isOnline() )
//                // If there is Internet, get the cache that was stored up to 60 seconds ago.
//                // After 60 seconds, refresh the cache.
//               {
//                request.newBuilder()
//                    .header("Cache-Control", "public, max-stale=" + 60)
//                    .build()
//              }
//              else
//              // If there is no Internet, get the cache that was stored up to 14 days ago.
//                request.newBuilder()
//                    .header("Cache-Control",
//                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 14)
//                    .build()
//              chain.proceed(request)
//            }
//            .build()
//
//        retrofit = Retrofit.Builder()
//            .client(client)
//            .addConverterFactory(MoshiConverterFactory.create(moshi))
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
//            .baseUrl(BASE_URL)
//            .build()
        // >>>>>>>>>>>>>>>>>>>>>>>>>>

        return binding.root
    }
}
