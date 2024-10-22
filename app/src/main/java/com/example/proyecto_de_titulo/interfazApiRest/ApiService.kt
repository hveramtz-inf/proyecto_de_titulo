package com.example.proyecto_de_titulo.interfazApiRest

import com.example.proyecto_de_titulo.dataApiRest.ApuntesApi
import com.example.proyecto_de_titulo.dataApiRest.CuestionarioApi
import com.example.proyecto_de_titulo.dataApiRest.CursoApi
import com.example.proyecto_de_titulo.dataApiRest.LoginRequest
import com.example.proyecto_de_titulo.dataApiRest.LoginResponse
import com.example.proyecto_de_titulo.dataApiRest.PreguntaApi
import com.example.proyecto_de_titulo.dataApiRest.PuntajeAlumnoCuestionario
import com.example.proyecto_de_titulo.dataApiRest.ReqCreateApunteApi
import com.example.proyecto_de_titulo.dataApiRest.RespuestaApi
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


interface CuestionarioApiService {
    @GET("cuestionarios/curso/{idcurso}")
    fun getCuestionarios(@Path("idcurso") idcurso: String): Call<List<CuestionarioApi>>

    @GET("cuestionarios/{id}")
    fun getCuestionarioById(@Path("id") id: String): Call<CuestionarioApi>

    @POST("cuestionarios")
    fun createCuestionario(@Body cuestionario: CuestionarioApi): Call<CuestionarioApi>

    @PUT("cuestionarios/{id}")
    fun updateCuestionario(@Path("id") id: String, @Body cuestionario: CuestionarioApi): Call<CuestionarioApi>

    @DELETE("cuestionarios/{id}")
    fun deleteCuestionario(@Path("id") id: String): Call<Void>
}

interface PreguntaApiService {
    @GET("preguntas")
    fun getPreguntas(): Call<List<PreguntaApi>>

    @GET("preguntas/{id}")
    fun getPreguntaById(@Path("id") id: String): Call<PreguntaApi>

    @GET("preguntas/cuestionario/{idcuestionario}")
    fun getPreguntasByCuestionario(@Path("idcuestionario") idCuestionario: String): Call<List<PreguntaApi>>

    @POST("preguntas")
    fun createPregunta(@Body pregunta: PreguntaApi): Call<PreguntaApi>

    @PUT("preguntas/{id}")
    fun updatePregunta(@Path("id") id: String, @Body pregunta: PreguntaApi): Call<PreguntaApi>

    @DELETE("preguntas/{id}")
    fun deletePregunta(@Path("id") id: String): Call<Void>
}

interface RespuestaApiService {
    @GET("respuestas")
    fun getRespuestas(): Call<List<RespuestaApi>>

    @GET("respuestas/{id}")
    fun getRespuestaById(@Path("id") id: String): Call<RespuestaApi>

    @GET("respuestas/pregunta/{idpregunta}")
    fun getRespuestasByPregunta(@Path("idpregunta") idPregunta: String): Call<List<RespuestaApi>>

    @POST("respuestas")
    fun createRespuesta(@Body respuesta: RespuestaApi): Call<RespuestaApi>

    @PUT("respuestas/{id}")
    fun updateRespuesta(@Path("id") id: String, @Body respuesta: RespuestaApi): Call<RespuestaApi>

    @DELETE("respuestas/{id}")
    fun deleteRespuesta(@Path("id") id: String): Call<Void>
}

interface PuntajeAlumnoCuestionarioApiService {
    @GET("puntajeCuestionario")
    fun getPuntajes(): Call<List<PuntajeAlumnoCuestionario>>

    @GET("puntajeCuestionario/{id}")
    fun getPuntajeById(@Path("id") id: String): Call<PuntajeAlumnoCuestionario>

    @GET("puntajeCuestionario/estudiante/{idestudiante}")
    fun getPuntajesByEstudiante(@Path("idestudiante") idEstudiante: String): Call<List<PuntajeAlumnoCuestionario>>

    @POST("puntajeCuestionario")
    fun createPuntaje(@Body puntaje: PuntajeAlumnoCuestionario): Call<PuntajeAlumnoCuestionario>

    @PUT("puntajeCuestionario/{id}")
    fun updatePuntaje(@Path("id") id: String, @Body puntaje: PuntajeAlumnoCuestionario): Call<PuntajeAlumnoCuestionario>

    @DELETE("puntajeCuestionario/{id}")
    fun deletePuntaje(@Path("id") id: String): Call<Void>
}


