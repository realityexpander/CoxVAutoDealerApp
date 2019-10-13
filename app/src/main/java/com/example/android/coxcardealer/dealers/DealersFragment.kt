
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
        // After navigating, call displayDealerComplete() so that the ViewModel is ready
        // for another navigation event.
        viewModel.navigateToSelectedDealer.observe(this, Observer {
          if ( null != it ) {
            // Must find the NavController from the Fragment
            this.findNavController().navigate(DealersFragmentDirections.actionShowVehicles(it))
            viewModel.displayVehiclesComplete()
          }
        })

        cacheDir = File(context?.cacheDir?.path + "/cox_cache" )
        client = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(pool)
            .cache(Cache(
                cacheDir,
                10L * 1024L * 1024L // 1 MiB
            ))
            .addInterceptor { chain ->
              var request = chain.request()
              request = if (isOnline() ) // (context?.let { hasNetwork(it) }!!)
              /*
              *  If there is Internet, get the cache that was stored 5 seconds ago.
              *  The 'max-age' attribute is responsible for this behavior.
              */ {
                println("HIT CACHE new: $request")
                request.newBuilder()
                    .header("Cache-Control", "public, max-stale=30, max-age=" + 600)
                    .build()
              }
              else
              /*
              *  If there is no Internet, get the cache that was stored 7 days ago.
              *  If the cache is older than 7 days, then discard it,
              *  The 'max-stale' attribute is responsible for this behavior.
              *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
              */
                request.newBuilder()
                    .header("Cache-Control",
                        "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                    .build()
              chain.proceed(request)
            }
            .build().also {
              println("CacheDir new: $cacheDir")
            }

            retrofit = Retrofit.Builder()
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(BASE_URL)
                .build()



        return binding.root
    }
}
