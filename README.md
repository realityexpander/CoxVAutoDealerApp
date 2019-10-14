Cox vAuto Dealerships App
===================================

Introduction
------------

CoxVAutoDealerApp is a demo app that shows available dealerships and vehicles for sale thru the Cox legacy API.
The vehicle data is stored on a Web server as a REST web service.  This app demonstrates
the use of [Retrofit](https://square.github.io/retrofit/) to make REST requests to the 
web service, [Moshi](https://github.com/square/moshi) to handle the deserialization of the 
returned JSON to Kotlin data objects. Uses OkHttp for caching.

The app also leverages [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel),
[LiveData](https://developer.android.com/topic/libraries/architecture/livedata), 
[Data Binding](https://developer.android.com/topic/libraries/data-binding/) with binding 
adapters, and [Navigation](https://developer.android.com/topic/libraries/architecture/navigation/) 
with the SafeArgs plugin for parameter passing between fragments.


Getting Started
---------------

1. Download and run the app.
