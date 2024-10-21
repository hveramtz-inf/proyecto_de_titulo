// RetrofitClient.kt
package com.example.proyecto_de_titulo.interfazApiRest

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://easy-economy.fly.dev/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val cursoApiService: CursoApiService by lazy {
        retrofit.create(CursoApiService::class.java)
    }

    val seccionApiService: SeccionApiService by lazy {
        retrofit.create(SeccionApiService::class.java)
    }
}