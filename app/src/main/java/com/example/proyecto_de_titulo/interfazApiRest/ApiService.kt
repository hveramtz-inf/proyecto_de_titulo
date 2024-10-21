package com.example.proyecto_de_titulo.interfazApiRest

import com.example.proyecto_de_titulo.dataApiRest.ApuntesApi
import com.example.proyecto_de_titulo.dataApiRest.CursoApi
import com.example.proyecto_de_titulo.dataApiRest.LoginRequest
import com.example.proyecto_de_titulo.dataApiRest.LoginResponse
import com.example.proyecto_de_titulo.dataApiRest.ReqCreateApunteApi
import com.example.proyecto_de_titulo.dataApiRest.SeccionApi
import com.example.proyecto_de_titulo.dataApiRest.reqUpdateApunteApi
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CursoApiService {
    @GET("cursos")
    fun getCursos(): Call<List<CursoApi>>
}

interface SeccionApiService {
    @GET("secciones")
    fun getSecciones(): Call<List<SeccionApi>>
}

interface AlumnoApiService {
    @POST("/estudiantes/iniciarSesion")
    fun IniciarSesion(@Body loginRequest: LoginRequest): Call<LoginResponse>
}

interface ApuntesApiService {
    @GET("apuntes/estudiante/{idestudiante}/seccion/{idseccion}")
    fun getApunte(
        @Path("idestudiante") idEstudiante: String,
        @Path("idseccion") idSeccion: String?
    ): Call<ApuntesApi>
    // Add other API methods for ApuntesApi here

    @POST("apuntes")
    fun createApunte(@Body reqApuntesApi: ReqCreateApunteApi): Call<ApuntesApi>

    // Actualizar un apunte
    @PUT("apuntes/{id}")
    fun updateApunte(
        @Path("id") id: String,
        @Body reqUpdateApunteApi: reqUpdateApunteApi
    ): Call<ApuntesApi>

    // Eliminar un apunte
    @DELETE("apuntes/{id}")
    fun deleteApunte(@Path("id") id: String): Call<Void>
}
