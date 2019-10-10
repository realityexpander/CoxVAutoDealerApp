package com.example.android.marsrealestate.network


/**
 * Created by Chris Athanas on 2019-10-08.
 */

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

/**
 * This data class defines a dealership property which includes an ID and the name of
 * the dealership
 */
@Parcelize
data class Dealer (
    val dealerId: Int? = null,
    val name: String? = null,
    val vehicles: List<Vehicle>? = null) : Parcelable {
  val dealerIdStr
    get() = dealerId.toString()
}

@Parcelize
data class Vehicle (
    val vehicleId: Int? = null,
    val year: Int? = null,
    val make: String? = null,
    val model: String? = null ) : Parcelable {
  val vehicleIdStr
    get() = "Vehicle ID: $vehicleId"
  val yearStr
    get() = "Year: $year"
  val yearModelStr
    get() = "$year $model"
}

@Parcelize
data class Dealers (
    val dealers: List<Dealer>? = null ) : Parcelable

