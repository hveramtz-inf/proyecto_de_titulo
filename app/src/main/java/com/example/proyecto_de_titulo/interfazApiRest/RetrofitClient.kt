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

    val apuntesApiService: ApuntesApiService by lazy {
        retrofit.create(ApuntesApiService::class.java)
    }

    val cuestionarioApiService: CuestionarioApiService by lazy {
        retrofit.create(CuestionarioApiService::class.java)
    }

    val preguntaApiService: PreguntaApiService by lazy {
        retrofit.create(PreguntaApiService::class.java)
    }

    val respuestaApiService: RespuestaApiService by lazy {
        retrofit.create(RespuestaApiService::class.java)
    }

    val puntajeAlumnoCuestionarioApiService: PuntajeAlumnoCuestionarioApiService by lazy {
        retrofit.create(PuntajeAlumnoCuestionarioApiService::class.java)
    }

    val calculadoraApiService: CalculadoraApiService by lazy {
        retrofit.create(CalculadoraApiService::class.java)
    }

    val historialCalculadoraApiService: HistorialCalculadoraApiService by lazy {
        retrofit.create(HistorialCalculadoraApiService::class.java)
    }

    val variableHistorialApiService: VariableHistorialApiService by lazy {
        retrofit.create(VariableHistorialApiService::class.java)
    }

}