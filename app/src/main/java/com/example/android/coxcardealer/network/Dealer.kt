package com.example.android.coxcardealer.network

/**
 * Created by Chris Athanas on 2019-10-08.
 */

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * This data class defines a dealership property which includes an ID and the name of
 * the dealership.
 */
@Parcelize
data class Dealer (
    val dealerId: Int? = null,
    val name: String? = null,
    val vehicles: MutableList<Vehicle>? = ArrayList() ) : Parcelable {
  val dealerIdStr
    get() = dealerId.toString()
}

/**
 * This data class describes an individual vehicle.
 */
@Parcelize
data class Vehicle (
    val vehicleId: Int? = null,
    val year: Int? = null,
    val make: String? = null,
    val model: String? = null,
    val dealerId: Int? = null) : Parcelable {
  val vehicleIdStr
    get() = "$vehicleId"
  val vehicleIdFullStr
    get() = "Vehicle ID: $vehicleIdStr"
  val yearStr
    get() = "Year: $year"
  val yearModelStr
    get() = "$year $model"
}

@Parcelize
data class Dealers (
    val dealers: List<Dealer>? = null ) : Parcelable

@Parcelize
data class DatasetId(
    val datasetId: String? = null ) : Parcelable

@Parcelize
data class Vehicles (
    val vehicleIds: List<Int>? = null ) : Parcelable
