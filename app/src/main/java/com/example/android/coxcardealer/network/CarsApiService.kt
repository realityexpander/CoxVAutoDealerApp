package com.example.android.coxcardealer.network

import android.content.Context
import android.os.Environment
import androidx.core.os.EnvironmentCompat
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



private const val BASE_URL = "https://api.coxauto-interview.com/"
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
private val REWRITE_CACHE_CONTROL_INTERCEPTOR = Interceptor { chain ->
    val originalResponse = chain.proceed(chain.request())
    println("OriginalResponse : $originalResponse")
    originalResponse.newBuilder()
        .removeHeader("Pragma")
        .addHeader("Cache-Control", String.format("max-age=%d", 60))
        .build()
}
//val dispatcher: Dispatcher = Dispatcher(Executors.newFixedThreadPool(20)).apply {
val dispatcher: Dispatcher = Dispatcher().apply {
    this.maxRequests = 40
    this.maxRequestsPerHost = 20
}
//var cacheDir = File(Environment.getDownloadCacheDirectory().path + "/cached_api3").also {
//    if (!it.exists()) {  // ** testing
//        it.mkdirs()
//    }
//}
//var cacheDir = File(Environment.getExternalStorageDirectory().path + "/cached_api4").also {
//    if (!it.exists()) {  // ** testing
//        it.mkdirs()
//    }
//}
var cacheDir = File(Environment.getDataDirectory().path + "/cache/cache_api5").also {
    if(!it.exists()) {
        it.mkdirs()
    }
}
//var cacheDir = File("/cache2/cache_api6").also {
//    if(!it.exists()) {
//        it.mkdirs()
//    }
//}
var pool = ConnectionPool(20, 1000, TimeUnit.MILLISECONDS)
val client: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
    .dispatcher(dispatcher)
    .connectionPool(pool)
//    .cache(Cache(
//        cacheDir,
//        10L * 1024L * 1024L // 1 MiB
//    ))
    .build().also {
//        it.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR)
        println("CacheDir : $cacheDir")
    }

private val retrofit = Retrofit.Builder()
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
}

// Retrofit call wrapper includes the datasetId var
fun getDealersCheatUsingDatasetIdAsync() : Deferred<Dealers> {
    return CarsApi.retrofitService.getDealersCheatAsync(datasetId!!)
}
