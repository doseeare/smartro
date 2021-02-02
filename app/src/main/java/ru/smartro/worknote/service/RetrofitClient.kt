package ru.smartro.worknote.service

import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient(context: Context) {

    private val authInterceptor = Interceptor { chain ->
        val newUrl = chain.request().url
            .newBuilder()
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer " + AppPreferences.accessToken)
            .url(newUrl)
            .build()
        chain.proceed(newRequest)
    }

    private var httpLoggingInterceptor = run {
        val httpLoggingInterceptor1 = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.d("okhttp", message)
            }
        })
        httpLoggingInterceptor1.apply {
            httpLoggingInterceptor1.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private val client =
        OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .authenticator(TokenAuthenticator(context))
            .connectTimeout(240, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(240, TimeUnit.SECONDS)
            .build()

    private fun retrofit(baseUrl: String) =
        Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    fun apiService(isWorkNote: Boolean): ApiService {
        return if (isWorkNote)
            retrofit("https://worknote-back.stage.smartro.ru/api/").create(ApiService::class.java)
        else
            retrofit("https://auth.stage.smartro.ru/api/").create(ApiService::class.java)
    }

    /*   fun apiService(isWorkNote: Boolean): ApiService {
           return if (isWorkNote)
               retrofit("https://worknote-back.rc.smartro.ru/api/").create(ApiService::class.java)
           else
               retrofit("https://auth.rc.smartro.ru/api/").create(ApiService::class.java)
       }*/

}