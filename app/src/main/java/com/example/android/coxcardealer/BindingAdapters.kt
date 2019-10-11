package com.example.android.coxcardealer

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.coxcardealer.network.Dealer
import com.example.android.coxcardealer.network.Vehicle
import com.example.android.coxcardealer.overview.CarsApiStatus
import com.example.android.coxcardealer.overview.PhotoGridAdapter
import com.example.android.coxcardealer.vehicles.VehiclesListAdapter

/**
 * When there is no [Dealer] data (data is null), hide the [RecyclerView],
 * otherwise show it.
 */
@BindingAdapter("dealerListData")
fun bindRecyclerViewToDealer(recyclerView: RecyclerView, data: List<Dealer>?) {
    val adapter = recyclerView.adapter as PhotoGridAdapter
    adapter.submitList(data)
}

/**
 * When there is no [Vehicle] data (data is null), hide the [RecyclerView],
 * otherwise show it.
 */
// ** testing
@BindingAdapter("vehicleListData")
fun bindRecyclerViewToVehicle(recyclerView: RecyclerView, data: List<Vehicle>?) {
    val adapter = recyclerView.adapter as VehiclesListAdapter
    adapter.submitList(data)
}

/**
 * Uses the Glide library to load an image by URL into an [ImageView]
 */
@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
                .load(imgUri)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
    }
}

/**
 * This binding adapter displays the [CarsApiStatus] of the network request in an image view.  When
 * the request is loading, it displays a loading_animation.  If the request has an error, it
 * displays a broken image to reflect the connection error.  When the request is finished, it
 * hides the image view.
 */
@BindingAdapter("carsApiStatus")
fun bindStatus(statusImageView: ImageView, status: CarsApiStatus?) {
    when (status) {
        CarsApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.loading_animation)
        }
        CarsApiStatus.ERROR -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        CarsApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
    }
}
