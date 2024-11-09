package com.example.proyecto_de_titulo.interfazApiRest

import com.example.proyecto_de_titulo.dataApiRest.ApuntesApi
import com.example.proyecto_de_titulo.dataApiRest.CalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.CuestionarioApi
import com.example.proyecto_de_titulo.dataApiRest.CursoApi
import com.example.proyecto_de_titulo.dataApiRest.FavoritosCalculadora
import com.example.proyecto_de_titulo.dataApiRest.FavoritosCuestionario
import com.example.proyecto_de_titulo.dataApiRest.HistorialCalculadoraApi
import com.example.proyecto_de_titulo.dataApiRest.LoginRequest
import com.example.proyecto_de_titulo.dataApiRest.LoginResponse
import com.example.proyecto_de_titulo.dataApiRest.PreguntaApi
import com.example.proyecto_de_titulo.dataApiRest.ProgresoCursoApi
import com.example.proyecto_de_titulo.dataApiRest.PuntajeAlumnoCuestionario
import com.example.proyecto_de_titulo.dataApiRest.ReqCreateApunteApi
import com.example.proyecto_de_titulo.dataApiRest.RespuestaApi
import com.example.proyecto_de_titulo.dataApiRest.SeccionApi
import com.example.proyecto_de_titulo.dataApiRest.SeccionRevisadaApi
import com.example.proyecto_de_titulo.dataApiRest.VariableHistorialApi
import com.example.proyecto_de_titulo.dataApiRest.reqUpdateApunteApi
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CursoApiService {
    @GET("movil/cursos")
    fun getCursos(): Call<List<CursoApi>>
    @GET("movil/cursos/clavepucv/{clave}")
    fun getCursoByClavePucv(@Path("clave") clave: String): Call<List<CursoApi>>
}

interface SeccionApiService {
    @GET("movil/secciones")
    fun getSecciones(): Call<List<SeccionApi>>
}

interface AlumnoApiService {
    @POST("movil/estudiantes/iniciarSesion")
    fun IniciarSesion(@Body loginRequest: LoginRequest): Call<LoginResponse>
}

interface ApuntesApiService {
    @GET("movil/apuntes/estudiante/{idestudiante}/seccion/{idseccion}")
    fun getApunte(
        @Path("idestudiante") idEstudiante: String,
        @Path("idseccion") idSeccion: String?
    ): Call<ApuntesApi>

    @POST("movil/apuntes")
    fun createApunte(@Body reqApuntesApi: ReqCreateApunteApi): Call<ApuntesApi>

    @PUT("movil/apuntes/{id}")
    fun updateApunte(
        @Path("id") id: String,
        @Body reqUpdateApunteApi: reqUpdateApunteApi
    ): Call<ApuntesApi>

    @DELETE("movil/apuntes/{id}")
    fun deleteApunte(@Path("id") id: String): Call<Void>
}

interface CuestionarioApiService {
    @GET("movil/cuestionarios/clavepucv/{clavepucv}")
    fun getCuestionariosByClavePucv(@Path("clavepucv") clavePucv: String): Call<List<CuestionarioApi>>
}

interface PreguntaApiService {
    @GET("movil/preguntas")
    fun getPreguntas(): Call<List<PreguntaApi>>

    @GET("movil/preguntas/{id}")
    fun getPreguntaById(@Path("id") id: String): Call<PreguntaApi>

    @GET("movil/preguntas/cuestionario/{idcuestionario}")
    fun getPreguntasByCuestionario(@Path("idcuestionario") idCuestionario: String): Call<List<PreguntaApi>>

    @POST("movil/preguntas")
    fun createPregunta(@Body pregunta: PreguntaApi): Call<PreguntaApi>

    @PUT("movil/preguntas/{id}")
    fun updatePregunta(@Path("id") id: String, @Body pregunta: PreguntaApi): Call<PreguntaApi>

    @DELETE("movil/preguntas/{id}")
    fun deletePregunta(@Path("id") id: String): Call<Void>
}

interface RespuestaApiService {
    @GET("movil/respuestas")
    fun getRespuestas(): Call<List<RespuestaApi>>

    @GET("movil/respuestas/{id}")
    fun getRespuestaById(@Path("id") id: String): Call<RespuestaApi>

    @GET("movil/respuestas/pregunta/{idpregunta}")
    fun getRespuestasByPregunta(@Path("idpregunta") idPregunta: String): Call<List<RespuestaApi>>

    @POST("movil/respuestas")
    fun createRespuesta(@Body respuesta: RespuestaApi): Call<RespuestaApi>

    @PUT("movil/respuestas/{id}")
    fun updateRespuesta(@Path("id") id: String, @Body respuesta: RespuestaApi): Call<RespuestaApi>

    @DELETE("movil/respuestas/{id}")
    fun deleteRespuesta(@Path("id") id: String): Call<Void>
}

interface PuntajeAlumnoCuestionarioApiService {
    @GET("movil/puntajeCuestionario")
    fun getPuntajes(): Call<List<PuntajeAlumnoCuestionario>>

    @GET("movil/puntajeCuestionario/{id}")
    fun getPuntajeById(@Path("id") id: String): Call<PuntajeAlumnoCuestionario>

    @GET("movil/puntajeCuestionario/estudiante/{idestudiante}")
    fun getPuntajesByEstudiante(@Path("idestudiante") idEstudiante: String): Call<List<PuntajeAlumnoCuestionario>>

    @POST("movil/puntajeCuestionario")
    fun createPuntaje(@Body puntaje: PuntajeAlumnoCuestionario): Call<PuntajeAlumnoCuestionario>

    @PUT("movil/puntajeCuestionario/{id}")
    fun updatePuntaje(@Path("id") id: String, @Body puntaje: PuntajeAlumnoCuestionario): Call<PuntajeAlumnoCuestionario>

    @DELETE("movil/puntajeCuestionario/{id}")
    fun deletePuntaje(@Path("id") id: String): Call<Void>
}

interface CalculadoraApiService {
    @GET("movil/calculadoras")
    fun getCalculadoras(): Call<List<CalculadoraApi>>

    @GET("movil/calculadoras/clavepucv/{id}")
    fun getCalculadorasByClavePucv(@Path("id") idClavePucv: String): Call<List<CalculadoraApi>>

    @GET("movil/calculadoras/{id}")
    fun getCalculadoraById(@Path("id") id: String): Call<CalculadoraApi>

    @POST("movil/calculadoras")
    fun createCalculadora(@Body calculadora: CalculadoraApi): Call<CalculadoraApi>

    @PUT("movil/calculadoras/{id}")
    fun updateCalculadora(@Path("id") id: String, @Body calculadora: CalculadoraApi): Call<CalculadoraApi>

    @DELETE("movil/calculadoras/{id}")
    fun deleteCalculadora(@Path("id") id: String): Call<Void>
}

interface HistorialCalculadoraApiService {
    @GET("movil/historialCalculadora")
    fun getHistorialCalculadoras(): Call<List<HistorialCalculadoraApi>>

    @GET("movil/historialCalculadora/{id}")
    fun getHistorialCalculadoraById(@Path("id") id: String): Call<HistorialCalculadoraApi>

    @GET("movil/historialCalculadora/calculadora/{idcalculadora}/estudiante/{idestudiante}")
    fun getHistorialCalculadoraByCalculadoraAndEstudiante(
        @Path("idcalculadora") idCalculadora: String,
        @Path("idestudiante") idEstudiante: String
    ): Call<List<HistorialCalculadoraApi>>

    @POST("movil/historialCalculadora")
    fun createHistorialCalculadora(@Body historialCalculadora: HistorialCalculadoraApi): Call<HistorialCalculadoraApi>

    @PUT("movil/historialCalculadora/{id}")
    fun updateHistorialCalculadora(@Path("id") id: String, @Body historialCalculadora: HistorialCalculadoraApi): Call<HistorialCalculadoraApi>

    @DELETE("movil/historialCalculadora/{id}")
    fun deleteHistorialCalculadora(@Path("id") id: String): Call<Void>
}

interface VariableHistorialApiService {
    @GET("movil/variableHistorial")
    fun getVariableHistorial(): Call<List<VariableHistorialApi>>

    @GET("movil/variableHistorial/{id}")
    fun getVariableHistorialById(@Path("id") id: String): Call<VariableHistorialApi>

    @GET("movil/variableHistorial/historial/{idhistorial}")
    fun getVariableHistorialByHistorial(@Path("idhistorial") idHistorial: String): Call<List<VariableHistorialApi>>

    @POST("movil/variableHistorial")
    fun createVariableHistorial(@Body variableHistorial: VariableHistorialApi): Call<VariableHistorialApi>

    @PUT("movil/variableHistorial/{id}")
    fun updateVariableHistorial(@Path("id") id: String, @Body variableHistorial: VariableHistorialApi): Call<VariableHistorialApi>

    @DELETE("movil/variableHistorial/{id}")
    fun deleteVariableHistorial(@Path("id") id: String): Call<Void>
}

interface FavoritoCalculadoraApiService {
    @GET("movil/favoritosCalculadora")
    fun getFavoritosCalculadora(): Call<List<FavoritosCalculadora>>

    @GET("movil/favoritosCalculadora/estudiante/{id}")
    fun getFavoritosCalculadoraByEstudiante(@Path("id") idEstudiante: String): Call<List<FavoritosCalculadora>>

    @GET("movil/favoritosCalculadora/{id}")
    fun getFavoritoCalculadoraById(@Path("id") id: String): Call<FavoritosCalculadora>

    @POST("movil/favoritosCalculadora")
    fun createFavoritoCalculadora(@Body favoritoCalculadora: FavoritosCalculadora): Call<FavoritosCalculadora>

    @DELETE("movil/favoritosCalculadora/{id}")
    fun deleteFavoritoCalculadora(@Path("id") id: String): Call<Void>
}

interface FavoritoCuestionarioApiService {
    @GET("movil/favoritosCuestionario")
    fun getFavoritosCuestionario(): Call<List<FavoritosCuestionario>>

    @GET("movil/favoritosCuestionario/{id}")
    fun getFavoritoCuestionarioById(@Path("id") id: String): Call<FavoritosCuestionario>

    @GET("movil/favoritosCuestionario/estudiante/{id}")
    fun getFavoritosCuestionarioByEstudiante(@Path("id") idEstudiante: String): Call<List<FavoritosCuestionario>>

    @POST("movil/favoritosCuestionario")
    fun createFavoritoCuestionario(@Body favoritoCuestionario: FavoritosCuestionario): Call<FavoritosCuestionario>

    @DELETE("movil/favoritosCuestionario/{id}")
    fun deleteFavoritoCuestionario(@Path("id") id: String): Call<Void>
}

interface ProgresoCursoApiService {
    @GET("movil/progresoCurso")
    fun getProgresoCurso(): Call<List<ProgresoCursoApi>>

    @GET("movil/progresoCurso/{id}")
    fun getProgresoCursoById(@Path("id") id: String): Call<ProgresoCursoApi>

    @GET("movil/progresoCurso/estudiante/{id}")
    fun getProgresoCursoByEstudiante(@Path("id") id: String): Call<List<ProgresoCursoApi>>

    @POST("movil/progresoCurso")
    fun createProgresoCurso(@Body progresoCurso: ProgresoCursoApi): Call<ProgresoCursoApi>

    @PUT("movil/progresoCurso/{id}")
    fun updateProgresoCurso(@Path("id") id: String, @Body progresoCurso: ProgresoCursoApi): Call<ProgresoCursoApi>

    @DELETE("movil/progresoCurso/{id}")
    fun deleteProgresoCurso(@Path("id") id: String): Call<Void>
}

interface SeccionRevisadaApiService {
    @GET("movil/seccionRevisada")
    fun getSeccionesRevisadas(): Call<List<SeccionRevisadaApi>>

    @GET("movil/seccionRevisada/{id}")
    fun getSeccionRevisadaById(@Path("id") id: String): Call<SeccionRevisadaApi>

    @GET("movil/seccionRevisada/estudiante/{id}/seccion/{idseccion}")
    fun getSeccionRevisadaByEstudianteAndSeccion(
        @Path("id") idEstudiante: String,
        @Path("idseccion") idSeccion: String
    ): Call<List<SeccionRevisadaApi>>

    @POST("movil/seccionRevisada")
    fun createSeccionRevisada(@Body seccionRevisada: SeccionRevisadaApi): Call<SeccionRevisadaApi>

    @PUT("movil/seccionRevisada/{id}")
    fun updateSeccionRevisada(@Path("id") id: String, @Body seccionRevisada: SeccionRevisadaApi): Call<SeccionRevisadaApi>

    @DELETE("movil/seccionRevisada/{id}")
    fun deleteSeccionRevisada(@Path("id") id: String): Call<Void>
}