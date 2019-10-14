package com.example.android.coxcardealer.network

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import com.example.android.coxcardealer.dealers.DealersFragment
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import kotlinx.coroutines.Deferred
import okhttp3.*
import retrofit2.http.Path
import java.io.File
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit



const val BASE_URL = "https://api.coxauto-interview.com/"

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

/**
 * Checks for full internet availability by pinging Google DNS server. (Not just "wifi is on.")
 * This can be called from anywhere, as it relies on the Android OS linux shell.
 */
fun isOnline(): Boolean {
    val runtime = Runtime.getRuntime()
    try {
        val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
        val exitValue = ipProcess.waitFor()
        return exitValue == 0
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: InterruptedException) {
        e.printStackTrace()
    }

    return false
}

/**
 * Use the OkHttp3 & Retrofit builder to build a retrofit object using a Moshi converter
 */
private val dispatcher: Dispatcher = Dispatcher().apply {
    this.maxRequests = 20
    this.maxRequestsPerHost = 10
}
private var cacheDir = File("default")
private var pool = ConnectionPool(10, 15000, TimeUnit.MILLISECONDS)
var client: OkHttpClient = OkHttpClient.Builder().build()
var retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .build()

fun setupRetrofitAndOkHttpClient(context: Context?) {
    context?.let {
        cacheDir = File(context.cacheDir?.path + "/cox_cache")
        client = OkHttpClient.Builder()
            .dispatcher(dispatcher)
            .connectionPool(pool)
            .cache(Cache(
                cacheDir,
                10L * 1024L * 1024L // 1 MiB
            ))
            .addInterceptor { chain ->
                var request = chain.request()
                request = if (isOnline()) {
                    // If there is Internet, get the cache that was stored up to 600 seconds ago.
                    // After 60 seconds, force refresh the cache.
                    request.newBuilder()
                        .header("Cache-Control", "public, max-stale=" + 600)
                        .build()
                } else {
                    // If there is no Internet, use the cache that was stored up to 14 days ago.
                    request.newBuilder()
                        .header("Cache-Control",
                            "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 14)
                        .build()
                }
                chain.proceed(request)
            }
            .build()

        retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .baseUrl(BASE_URL)
            .build()
    }
}

/**
 * A public interface to expose the various API access methods
 * Returns a Coroutine to fetch with await()
 */
interface DealersApiService {

    @GET("/api/datasetId")
    fun getDatasetIdAsync():
        Deferred<DatasetId>

    @GET("/api/{datasetId}/cheat")
    fun getDealersCheatAsync(@Path("datasetId") datasetId: String):
        Deferred<Dealers>

    @GET("/api/{datasetId}/vehicles")
    fun getVehiclesAsync(@Path("datasetId") datasetId: String):
        Deferred<Vehicles>

    @GET("/api/{datasetId}/vehicles/{vehicleId}")
    fun getVehicleInfoAsync(@Path("datasetId") datasetId: String,
                            @Path("vehicleId") vehicleId: Int):
        Deferred<Vehicle>

    @GET("/api/{datasetId}/dealers/{dealerId}")
    fun getDealersInfoAsync(@Path("datasetId") datasetId: String,
                            @Path("dealerId") dealerId: Int?):
        Deferred<Dealer>
}

/**
 * A public Api object to expose the lazy-initialized Retrofit service
 */
object CarsApi {
    val retrofitService : DealersApiService by lazy { retrofit.create(DealersApiService::class.java) }
//    val retrofitService : DealersApiService =  retrofit.create(DealersApiService::class.java)
}

