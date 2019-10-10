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

private const val BASE_URL = "https://api.coxauto-interview.com/api/"
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

/**
 * A public interface to expose the [getDealersAsync] method
 * Returns a Coroutine [Deferred] [List] of [Dealers] to fetch with await()
 */
interface DealersApiService {
    @GET("/api/${DATASET_ID}/cheat")
    fun getDealersAsync():
        Deferred<Dealers>
}

/**
 * A public Api object to expose the lazy-initialized Retrofit service
 */
object CarsApi {
    val retrofitService2 : DealersApiService by lazy { retrofit.create(DealersApiService::class.java) }
}