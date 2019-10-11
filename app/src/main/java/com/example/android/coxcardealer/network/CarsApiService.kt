package com.example.android.coxcardealer.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import kotlinx.coroutines.Deferred
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://api.coxauto-interview.com/"
private const val DATASET_ID = "4x7xITpJ1wg"
var datasetId: String? = null

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

/**
 * A public interface to expose the various API access methods
 * Returns a Coroutine to fetch with await()
 */
interface DealersApiService {

    @GET("/api/datasetId")
    fun getDatasetIdAsync():
        Deferred<DatasetId>

    @GET("/api/{datasetId}/cheat")
    fun getDealersAsync(@Path("datasetId") datasetId: String):
      Deferred<Dealers>

    @GET("/api/{datasetId}/vehicles")
    fun getVehiclesAsync(@Path("datasetId") datasetId: String) :
        Deferred<Vehicles>

    @GET("/api/{datasetId}/vehicles/{vehicleId}")
    fun getVehiclesInfoAsync(@Path("datasetId") datasetId: String) :
        Deferred<Vehicle>

    @GET("/api/{datasetId}/dealers/{dealerId}")
    fun getDealersInfoAsync(@Path("datasetId") type: String,
                            @Path("dealerId") dealerId: String)
}

/**
 * A public Api object to expose the lazy-initialized Retrofit service
 */
object CarsApi {
    val retrofitService : DealersApiService by lazy { retrofit.create(DealersApiService::class.java) }
}

// Retrofit call wrapper includes the datasetId
fun getDealersUsingDatasetIdAsync() : Deferred<Dealers> {
    return CarsApi.retrofitService.getDealersAsync(datasetId!!)
}
