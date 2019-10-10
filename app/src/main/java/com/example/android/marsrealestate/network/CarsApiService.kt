package com.example.android.marsrealestate.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.http.Query

enum class CarsApiFilter(val value: String) {
    SHOW_RENT("rent"),
    SHOW_BUY("buy"),
    SHOW_ALL("all") }

private const val BASE_URL = "https://android-kotlin-fun-mars-server.appspot.com/"

private const val BASE_URL2 = "https://api.coxauto-interview.com/api/"
private const val DATASET_ID = "4x7xITpJ1wg"

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

private val retrofit2 = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL2)
    .build()

/**
 * A public interface that exposes the [getPropertiesAsync] method
 */
interface MarsApiService {
    /**
     * Returns a Coroutine [Deferred] [List] of [MarsProperty] which can be fetched with await() if
     * in a Coroutine scope.
     * The @GET annotation indicates that the "realestate" endpoint will be requested with the GET
     * HTTP method
     */
    // ** delete
    @GET("realestate")
    fun getPropertiesAsync(@Query("filter") type: String):
            Deferred<List<MarsProperty>>
}

interface DealersApiService {
    @GET("/api/${DATASET_ID}/cheat")
    fun getDealersAsync():
        Deferred<Dealers>

    @GET("/api/${DATASET_ID}/cheat")
    fun getDealers(): Call<Dealers>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object MarsApi {
    val retrofitService : MarsApiService by lazy { retrofit.create(MarsApiService::class.java) }
}

object CarsApi {
    val retrofitService2 : DealersApiService by lazy { retrofit2.create(DealersApiService::class.java) }
}