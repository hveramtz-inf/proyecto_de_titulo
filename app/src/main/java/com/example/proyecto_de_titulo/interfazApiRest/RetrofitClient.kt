// RetrofitClient.kt
package com.example.proyecto_de_titulo.interfazApiRest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {
    private const val BASE_URL = "https://easy-economy.fly.dev/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val cursoApiService: CursoApiService by lazy {
        retrofit.create(CursoApiService::class.java)
    }

    val seccionApiService: SeccionApiService by lazy {
        retrofit.create(SeccionApiService::class.java)
    }

    val alumnoApiService: AlumnoApiService by lazy {
        retrofit.create(AlumnoApiService::class.java)
    }
}