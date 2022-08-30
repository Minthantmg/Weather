package com.example.weather_test

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AppIdInterceptor : Interceptor {

    companion object{
        private const val API_KEY = "15ad2d1c2e624472f8504245bcd7393f"
    }
    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url

        val newUrl =url.newBuilder()
            .addQueryParameter("appid", API_KEY)
            .build()
        val request = Request.Builder().url(newUrl).build()
        return chain.proceed(request)
    }
}