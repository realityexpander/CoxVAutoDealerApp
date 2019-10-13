package com.example.android.coxcardealer.network

import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
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
var datasetId: String? = null

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

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

fun hasNetwork(context: Context): Boolean? {
    var isConnected: Boolean? = false // Initial Value
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
    if (activeNetwork != null && activeNetwork.isConnected)
        isConnected = true
    return isConnected
}

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object.
 */
//private val REWRITE_CACHE_CONTROL_INTERCEPTOR = Interceptor { chain ->
//    val originalResponse = chain.proceed(chain.request())
//    println("OriginalResponse : $originalResponse")
//    originalResponse.newBuilder()
//        .removeHeader("Pragma")
//        .addHeader("Cache-Control", String.format("max-age=%d", 60))
//        .build()
//}

val dispatcher: Dispatcher = Dispatcher().apply {
    this.maxRequests = 40
    this.maxRequestsPerHost = 20
}


var cacheDir = File("hellothere")
//var cacheDir = File(android.os.Environment.getExternalStoragePublicDirectory(
//    Environment.DIRECTORY_NOTIFICATIONS).path ).also {
//        if (!it.exists()) {  // ** testing
//        it.mkdirs()
//    }
//}
//var cacheDir = File(Environment.getDownloadCacheDirectory().path + "/cached99").also {
//    if (!it.exists()) {  // ** testing
//        it.mkdirs()
//    }
//}
//var cacheDir = File(Environment.getExternalStorageDirectory().path + "/cached_api4").also {
//    if (!it.exists()) {  // ** testing
//        it.mkdirs()
//    }
//}
//var cacheDir = File(Environment.getDataDirectory().path + "/cache/cache_api5").also {
//    if(!it.exists()) {
//        it.mkdirs()
//    }
//}

var pool = ConnectionPool(20, 10000, TimeUnit.MILLISECONDS)
var client: OkHttpClient = OkHttpClient.Builder()
//    .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
    .dispatcher(dispatcher)
    .connectionPool(pool)
    .cache(Cache(
        cacheDir,
        10L * 1024L * 1024L // 1 MiB
    ))
    .addInterceptor { chain ->
        var request = chain.request()
        request = if (isOnline() )
        /*
        *  If there is Internet, get the cache that was stored 5 seconds ago.
        *  The 'max-age' attribute is responsible for this behavior.
        */ {
            println("HIT CACHE new: $request")
            request.newBuilder()
                .header("Cache-Control", "public,  max-age=" + 600)
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
        println("CacheDir old: $cacheDir")
    }

//private val retrofit = Retrofit.Builder()
var retrofit = Retrofit.Builder()
    .client(client)
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

// Retrofit call wrapper includes the datasetId var
fun getDealersCheatUsingDatasetIdAsync() : Deferred<Dealers> {
    return CarsApi.retrofitService.getDealersCheatAsync(datasetId!!)
}
