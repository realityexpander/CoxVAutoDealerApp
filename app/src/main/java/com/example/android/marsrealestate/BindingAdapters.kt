package com.example.android.marsrealestate

import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.android.marsrealestate.network.Dealer
import com.example.android.marsrealestate.network.MarsProperty
import com.example.android.marsrealestate.overview.CarsApiStatus
import com.example.android.marsrealestate.overview.PhotoGridAdapter

/**
 * When there is no Mars property data (data is null), hide the [RecyclerView], otherwise show it.
 */
// ** delete
//@BindingAdapter("listData")
//fun bindRecyclerView(recyclerView: RecyclerView, data: List<MarsProperty>?) {
//    val adapter = recyclerView.adapter as PhotoGridAdapter
//    adapter.submitList(data)
//}
@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Dealer>?) {
    val adapter = recyclerView.adapter as PhotoGridAdapter
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
@BindingAdapter("marsApiStatus")
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
