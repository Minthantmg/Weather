package com.example.weather_test

import android.Manifest
import android.annotation.SuppressLint
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.moshi.Moshi
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

class MainActivity : AppCompatActivity() {


    companion object {
        const val REQUEST_CODE_PERMISSION_LOCATION = 100
    }

    private val progressBar by lazy {
        findViewById<ProgressBar>(R.id.progressBar)
    }

    private val tvTemp by lazy {
        findViewById<TextView>(R.id.tvTemp)
    }

    private val ivWeather by lazy {
        findViewById<ImageView>(R.id.ivWeather)
    }

    private val tvCityName by lazy {
        findViewById<TextView>(R.id.tvCityName)
    }

    private val retrofit by lazy {
        RetrofitInstanceFactory.instance()
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getLocation()

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val locationManager =
                        this@MainActivity.getSystemService(LOCATION_SERVICE) as LocationManager
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    executeNetwork(
                        latitude = location?.latitude.toString(),
                        longitude = location?.longitude.toString()
                    )
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    p1: PermissionToken?
                ) {
                }

            })
            .check()

    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        tvTemp.visibility = View.GONE
        ivWeather.visibility = View.GONE
        tvCityName.visibility = View.GONE
    }

    private fun showData(
        temperature: String,
        cityName: String,
        weatherIcon: String
    ) {
        progressBar.visibility = View.GONE

        tvTemp.text = temperature
        tvCityName.text = cityName
        Glide.with(this).load(weatherIcon).into(ivWeather)


        tvTemp.visibility = View.VISIBLE
        ivWeather.visibility = View.VISIBLE
        tvCityName.visibility = View.VISIBLE
    }

    private fun executeNetwork(
        latitude: String,
        longitude: String
    ) {


        val openWeatherApi = retrofit.create(OpenWeatherMapAPI::class.java)

        openWeatherApi.geoCoordinate(
            latitude = latitude,
            longitude = longitude,
        ).enqueue(object : retrofit2.Callback<OpenWeatherMapResponse> {
            override fun onResponse(
                call: retrofit2.Call<OpenWeatherMapResponse>,
                response: retrofit2.Response<OpenWeatherMapResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { response ->
                        Log.i("response", response.toString())
                        val iconUrl = response.weather.getOrNull(0)?.icon ?: ""
                        val fullUrl = "https://openweathermap.org/img/wn/$iconUrl@2x.png"
                        showData(
                            temperature = response.main.temp,
                            cityName = response.name,
                            weatherIcon = fullUrl
                        )
                    }

                }
            }

            override fun onFailure(call: retrofit2.Call<OpenWeatherMapResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }


}
