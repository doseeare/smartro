package ru.smartro.worknote.service.network

import android.content.Context
import android.util.Log
import io.sentry.android.okhttp.SentryOkHttpInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.smartro.worknote.service.AppPreferences
import ru.smartro.worknote.service.network.interceptor.TokenAuthenticator
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
            .addInterceptor(SentryOkHttpInterceptor())
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
            .build()

    fun apiService(isWorkNote: Boolean): ApiService {
        // переключатель для разных API
        when (APIENUM.TEST) {
            APIENUM.TEST -> {
                return if (isWorkNote)
                    retrofit("https://worknote-back.stage.smartro.ru/api/fact/").create(ApiService::class.java)
                else
                    retrofit("https://auth.stage.smartro.ru/api/").create(ApiService::class.java)
            }
            APIENUM.PRODUCTION -> {
                return if (isWorkNote)
                    retrofit("https://wn-api.smartro.ru/api/fact/").create(ApiService::class.java)
                else
                    retrofit("https://auth.smartro.ru/api/").create(ApiService::class.java)
            }
            APIENUM.RC -> {
                return if (isWorkNote)
                    retrofit("https://worknote-back.rc.smartro.ru/api/fact/").create(ApiService::class.java)
                else
                    retrofit("https://auth.rc.smartro.ru/api/").create(ApiService::class.java)
            }
        }
    }

}

enum class APIENUM {
    TEST,
    PRODUCTION,
    RC
}